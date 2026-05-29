package com.wehear.service;

import com.wehear.model.Lesson;
import com.wehear.model.Quiz;
import com.wehear.model.QuizQuestion;
import com.wehear.model.SignDictionary;
import com.wehear.model.SignMedia;
import com.wehear.repository.LessonRepository;
import com.wehear.repository.QuizRepository;
import com.wehear.repository.SignDictionaryRepository;
import com.wehear.repository.SignMediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SignDictionaryRepository signRepository;
    private final SignMediaRepository mediaRepository;
    private final QuizRepository quizRepository;
    private final AIService aiService;

    public LessonService(LessonRepository lessonRepository,
                         SignDictionaryRepository signRepository, SignMediaRepository mediaRepository,
                         QuizRepository quizRepository, AIService aiService) {
        this.lessonRepository = lessonRepository;
        this.signRepository = signRepository;
        this.mediaRepository = mediaRepository;
        this.quizRepository = quizRepository;
        this.aiService = aiService;
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public List<Lesson> getAllLessonsWithQuiz() {
        List<Lesson> lessons = lessonRepository.findAll();
        for (Lesson lesson : lessons) {
            loadLessonQuiz(lesson);
        }
        return lessons;
    }

    public List<Lesson> getPublishedLessons() {
        return lessonRepository.findPublished();
    }

    public Lesson getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id);
        if (lesson != null) {
            loadLessonSigns(lesson);
            loadLessonQuiz(lesson);
        }
        return lesson;
    }

    public Lesson getLessonBySlug(String slug) {
        Lesson lesson = lessonRepository.findBySlug(slug);
        if (lesson != null) {
            loadLessonSigns(lesson);
            loadLessonQuiz(lesson);
        }
        return lesson;
    }

    private void loadLessonSigns(Lesson lesson) {
        List<Long> signIds = lessonRepository.findSignIdsByLessonId(lesson.getId());
        List<SignDictionary> signs = new ArrayList<>();
        for (Long signId : signIds) {
            SignDictionary sign = signRepository.findById(signId);
            if (sign != null) {
                sign.setMedia(mediaRepository.findBySignId(signId));
                signs.add(sign);
            }
        }
        lesson.setSigns(signs);
    }

    private void loadLessonQuiz(Lesson lesson) {
        Quiz quiz = quizRepository.findByLessonId(lesson.getId());
        if (quiz != null) {
            List<QuizQuestion> questions = quizRepository.findQuestionsByQuizId(quiz.getId());
            // Load media for each question if related to a sign
            for (QuizQuestion q : questions) {
                if (q.getRelatedSignId() != null) {
                    List<SignMedia> mediaList = mediaRepository.findBySignId(q.getRelatedSignId());
                    if (!mediaList.isEmpty()) {
                        q.setSignMedia(mediaList.get(0));
                    }
                }
            }
            quiz.setQuestions(questions);
            lesson.setQuiz(quiz);
        }
    }

    @Transactional
    public Long saveFullLesson(Lesson lesson, List<Long> signIds) {
        Long lessonId;
        if (lesson.getId() == null) {
            lessonId = lessonRepository.insert(lesson);
        } else {
            lessonRepository.update(lesson);
            lessonId = lesson.getId();
            lessonRepository.removeAllSignsFromLesson(lessonId);
        }

        if (signIds != null) {
            for (int i = 0; i < signIds.size(); i++) {
                lessonRepository.addSignToLesson(lessonId, signIds.get(i), i + 1);
            }
        }

        // Save Quiz if present
        if (lesson.getQuiz() != null) {
            Quiz quiz = lesson.getQuiz();
            quiz.setLessonId(lessonId);
            
            Long quizId;
            Quiz existingQuiz = quizRepository.findByLessonId(lessonId);
            
            if (existingQuiz == null) {
                quizId = quizRepository.insertQuiz(quiz);
            } else {
                quiz.setId(existingQuiz.getId());
                quizRepository.updateQuiz(quiz);
                quizId = existingQuiz.getId();
            }

            if (quiz.getQuestions() != null) {
                // Lấy danh sách câu hỏi hiện có trong DB để so sánh
                List<QuizQuestion> currentDbQuestions = quizRepository.findQuestionsByQuizId(quizId);
                List<Long> newQuestionIds = new ArrayList<>();

                for (QuizQuestion q : quiz.getQuestions()) {
                    q.setQuizId(quizId);
                    if (q.getId() != null && q.getId() > 0) {
                        // Nếu đã có ID, thực hiện update
                        quizRepository.updateQuestion(q);
                        newQuestionIds.add(q.getId());
                    } else {
                        // Nếu chưa có ID, thực hiện insert
                        quizRepository.insertQuestion(q);
                    }
                }

                // Xóa những câu hỏi không còn xuất hiện trong request mới
                // nhưng phải kiểm tra xem chúng đã được làm bài chưa
                for (QuizQuestion dbQ : currentDbQuestions) {
                    if (!newQuestionIds.contains(dbQ.getId())) {
                        quizRepository.deleteQuestionIfNotAttempted(dbQ.getId());
                    }
                }
            }
        }

        return lessonId;
    }

    @Transactional
    public boolean deleteLesson(Long id) {
        lessonRepository.removeAllSignsFromLesson(id);
        return lessonRepository.deleteById(id) > 0;
    }

    public Lesson generateAILesson(String prompt) {
        // Chuẩn hóa prompt để làm title đẹp hơn
        String cleanTopic = prompt.substring(0, 1).toUpperCase() + prompt.substring(1).toLowerCase();
        
        List<String> suggestedWords = aiService.suggestWordsForTheme(prompt);
        log.info("[LESSON SERVICE] AI Suggested Words for '{}': {}", prompt, suggestedWords);
        
        List<SignDictionary> matchedSigns = new ArrayList<>();
        List<String> foundWords = new ArrayList<>();
        Set<Long> addedSignIds = new HashSet<>();

        for (String word : suggestedWords) {
            List<SignDictionary> results = signRepository.findBySignWordExact(word.trim());
            
            if (!results.isEmpty()) {
                for (SignDictionary sign : results) {
                    if (!addedSignIds.contains(sign.getId())) {
                        sign.setMedia(mediaRepository.findBySignId(sign.getId()));
                        if (sign.getMedia() != null && !sign.getMedia().isEmpty()) {
                            matchedSigns.add(sign);
                            foundWords.add(sign.getSignWord());
                            addedSignIds.add(sign.getId());
                            break;
                        }
                    }
                }
            }
            if (matchedSigns.size() >= 25) break;
        }

        Lesson aiLesson = new Lesson();
        aiLesson.setTitle("Chủ đề: " + cleanTopic);
        aiLesson.setDescription("Khám phá các ký hiệu quan trọng liên quan đến '" + prompt + "'. Lộ trình được thiết kế cá nhân hóa cho bạn.");
        aiLesson.setSigns(matchedSigns);

        if (!foundWords.isEmpty()) {
            List<QuizQuestion> aiQuestions = aiService.generateQuizQuestions(foundWords);
            for (QuizQuestion q : aiQuestions) {
                matchedSigns.stream()
                        .filter(s -> s.getSignWord().trim().equalsIgnoreCase(q.getCorrectAnswer().trim()))
                        .findFirst()
                        .ifPresent(s -> {
                            q.setRelatedSignId(s.getId());
                            if (s.getMedia() != null && !s.getMedia().isEmpty()) {
                                q.setSignMedia(s.getMedia().get(0));
                            }
                        });
            }
            
            Quiz aiQuiz = new Quiz();
            aiQuiz.setTitle("Kiểm tra kiến thức: " + cleanTopic);
            aiQuiz.setQuestions(aiQuestions);
            aiQuiz.setPassingScore(new BigDecimal("70"));
            aiLesson.setQuiz(aiQuiz);
        }

        return aiLesson;
    }

    public List<QuizQuestion> generateQuizFromSigns(List<String> signWords) {
        List<QuizQuestion> questions = aiService.generateQuizQuestions(signWords);
        
        // Match each question with the existing sign IDs in the database
        for (QuizQuestion q : questions) {
            String correctAnswer = q.getCorrectAnswer().trim().toLowerCase();
            List<SignDictionary> matched = signRepository.findBySignWordExact(correctAnswer);
            if (!matched.isEmpty()) {
                SignDictionary sign = matched.get(0);
                q.setRelatedSignId(sign.getId());
                List<SignMedia> media = mediaRepository.findBySignId(sign.getId());
                if (!media.isEmpty()) {
                    q.setSignMedia(media.get(0));
                }
            }
        }
        return questions;
    }

    /**
     * Gợi ý từ vựng cho bài học dựa trên tiêu đề và mô tả.
     */
    public List<SignDictionary> suggestSignsForLesson(String title, String description) {
        // 1. Gọi AI để lấy danh sách từ vựng liên quan (AI Expansion)
        String context = title + " " + (description != null ? description : "");
        List<String> suggestedLabels = aiService.suggestRelevantSigns(context, null);

        // 2. Khớp từng từ khóa AI gợi ý với Database
        List<SignDictionary> result = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();

        for (String label : suggestedLabels) {
            // Tìm kiếm chính xác hoặc gần đúng trong DB
            List<SignDictionary> matches = signRepository.findBySignWordExact(label.trim());
            
            if (!matches.isEmpty()) {
                SignDictionary bestMatch = matches.get(0);
                if (!addedIds.contains(bestMatch.getId())) {
                    bestMatch.setMedia(mediaRepository.findBySignId(bestMatch.getId()));
                    result.add(bestMatch);
                    addedIds.add(bestMatch.getId());
                }
            }
        }

        // 3. Fallback: Nếu kết quả quá ít (dưới 5 từ), thử tìm thêm bằng từ khóa trực tiếp từ tiêu đề
        if (result.size() < 5) {
            String[] titleWords = title.split("\\s+");
            for (String tw : titleWords) {
                if (tw.length() < 2) continue;
                List<SignDictionary> matches = signRepository.findBySignWordExact(tw);
                for (SignDictionary m : matches) {
                    if (!addedIds.contains(m.getId())) {
                        m.setMedia(mediaRepository.findBySignId(m.getId()));
                        result.add(m);
                        addedIds.add(m.getId());
                    }
                }
                if (result.size() >= 15) break;
            }
        }

        return result;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LessonService.class);
}
