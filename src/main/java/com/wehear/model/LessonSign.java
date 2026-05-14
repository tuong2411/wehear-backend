package com.wehear.model;

public class LessonSign {
    private Long id;
    private Long lessonId;
    private Long signId;
    private int displayOrder;

    public LessonSign() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public Long getSignId() { return signId; }
    public void setSignId(Long signId) { this.signId = signId; }

    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}
