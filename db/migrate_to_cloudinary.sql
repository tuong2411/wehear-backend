-- Migration script to update video paths to Cloudinary URLs
-- Date: 14/05/2026

-- 1. Update sign_media table for Dictionary and Lesson videos
UPDATE sign_media 
SET media_url = REPLACE(media_url, '/media/dataset/', 'https://res.cloudinary.com/dnmzz8htk/video/upload/')
WHERE media_url LIKE '/media/dataset/%';

-- 2. Verify some changes
SELECT id, sign_id, media_url FROM sign_media WHERE media_url LIKE 'https://res.cloudinary.com/%' LIMIT 20;
