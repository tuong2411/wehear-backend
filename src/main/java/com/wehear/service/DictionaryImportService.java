package com.wehear.service;

import com.wehear.model.SignDictionary;
import com.wehear.model.SignMedia;
import com.wehear.repository.SignDictionaryRepository;
import com.wehear.repository.SignMediaRepository;
import com.wehear.util.SlugUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class DictionaryImportService {

    private final SignDictionaryRepository signRepository;
    private final SignMediaRepository mediaRepository;

    public DictionaryImportService(SignDictionaryRepository signRepository, SignMediaRepository mediaRepository) {
        this.signRepository = signRepository;
        this.mediaRepository = mediaRepository;
    }

    @Transactional
    public Map<String, Object> importFromCsv(String csvFilePath) throws IOException {
        // Key: label + "|" + regionCode
        Map<String, List<String>> groupToVideos = new LinkedHashMap<>();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath), StandardCharsets.UTF_8))) {
            String line;
            br.readLine(); // Skip header
            
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", 3);
                if (values.length < 3) continue;
                
                String videoFile = values[1].trim();
                String label = values[2].trim();
                
                // Determine region from filename
                String filenameNoExt = videoFile.contains(".") ? videoFile.substring(0, videoFile.lastIndexOf('.')) : videoFile;
                String regionSuffix = "";
                if (filenameNoExt.endsWith("B")) regionSuffix = "B";
                else if (filenameNoExt.endsWith("N")) regionSuffix = "N";
                else if (filenameNoExt.endsWith("T")) regionSuffix = "T";
                
                String key = label + "|" + regionSuffix;
                groupToVideos.computeIfAbsent(key, k -> new ArrayList<>()).add(videoFile);
            }
        }

        int signsImported = 0;
        int mediaImported = 0;

        for (Map.Entry<String, List<String>> entry : groupToVideos.entrySet()) {
            String[] parts = entry.getKey().split("\\|", -1);
            String labelText = parts[0];
            String regionSuffix = parts[1];
            List<String> videos = entry.getValue();

            // Map suffix to region name
            String regionName = "Toàn quốc";
            String codeSuffix = "";
            if ("B".equals(regionSuffix)) { regionName = "Miền Bắc"; codeSuffix = "_b"; }
            else if ("N".equals(regionSuffix)) { regionName = "Miền Nam"; codeSuffix = "_n"; }
            else if ("T".equals(regionSuffix)) { regionName = "Miền Trung"; codeSuffix = "_t"; }

            String labelCode = SlugUtil.toSlug(labelText).replace("-", "_") + codeSuffix;

            // Check if sign already exists
            SignDictionary existingSign = signRepository.findByLabelCode(labelCode);
            Long signId;

            if (existingSign == null) {
                SignDictionary newSign = new SignDictionary();
                newSign.setSignWord(labelText);
                newSign.setLabelCode(labelCode);
                newSign.setActive(true);
                newSign.setDescription("");
                newSign.setRegion(regionName);
                newSign.setDifficultyLevel("BASIC");
                
                signId = signRepository.insert(newSign);
                signsImported++;
            } else {
                signId = existingSign.getId();
            }

            // Import media
            for (String videoFile : videos) {
                SignMedia media = new SignMedia();
                media.setSignId(signId);
                media.setMediaType("VIDEO");
                media.setMediaUrl("/media/dataset/" + videoFile);
                media.setPrimary(videos.indexOf(videoFile) == 0);
                
                mediaRepository.insert(media);
                mediaImported++;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("signsImported", signsImported);
        result.put("mediaImported", mediaImported);
        result.put("totalGroupsProcessed", groupToVideos.size());
        return result;
    }
}
