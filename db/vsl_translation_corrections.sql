CREATE TABLE IF NOT EXISTS vsl_translation_corrections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_text TEXT NOT NULL,
    model_name VARCHAR(64) NOT NULL,
    model_translation TEXT NOT NULL,
    corrected_translation TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_vsl_correction_user_created (user_id, created_at),
    CONSTRAINT fk_vsl_correction_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
