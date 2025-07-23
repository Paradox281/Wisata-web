-- Mengubah return_date menjadi nullable karena tidak akan diisi
ALTER TABLE bookings 
    ALTER COLUMN return_date DROP NOT NULL;