-- SQL Migration for Dictionary Contribution System
CREATE TABLE IF NOT EXISTS dictionary_contributions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    word VARCHAR(255) NOT NULL,
    description TEXT,
    example TEXT,
    video_url VARCHAR(512) NOT NULL,
    type ENUM('NEW', 'EDIT') NOT NULL,
    target_dictionary_id BIGINT DEFAULT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    admin_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_contribution_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_contribution_target_dict FOREIGN KEY (target_dictionary_id) REFERENCES sign_dictionary(id)
);

CREATE INDEX idx_contribution_status ON dictionary_contributions(status);
CREATE INDEX idx_contribution_user ON dictionary_contributions(user_id);
