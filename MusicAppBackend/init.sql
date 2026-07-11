-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: music_app_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `artists`
--

DROP TABLE IF EXISTS `artists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artists` (
  `id` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `bio` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artists`
--

LOCK TABLES `artists` WRITE;
/*!40000 ALTER TABLE `artists` DISABLE KEYS */;
INSERT INTO `artists` VALUES ('A001','Binz','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580514/binz_frek6h.webp',NULL,'2026-07-10 12:32:47'),('A003','52Hz','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580511/52hz_r2zjez.webp',NULL,'2026-07-10 12:32:47'),('A004','HIEUTHUHAI','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/ht2_ypqbc1.jpg',NULL,'2026-07-10 12:32:47'),('A005','GREY D','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580517/greyD_oghkei.jpg',NULL,'2026-07-10 12:32:47'),('A006','Juky San','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580518/jukysan_rhzvis.jpg',NULL,'2026-07-10 12:32:47'),('A007','Dương Domic','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/duongdomic_rplmcw.jpg',NULL,'2026-07-10 12:32:47'),('A008','Wowy','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580515/wowy_au4mtt.jpg',NULL,'2026-07-10 12:32:47'),('A009','Tóc Tiên','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580512/toctien_xrfpij.webp',NULL,'2026-07-10 12:32:47'),('A010','MIN','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580518/min_prxc8s.webp',NULL,'2026-07-10 12:32:47'),('A011','OgeNus','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580520/ogenus_pythgn.jpg',NULL,'2026-07-10 12:32:47'),('A012','Phùng Khánh Linh','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/phungklinh_jzt9od.webp',NULL,'2026-07-10 12:32:47'),('A013','Phương Ly','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/phuongly_ewgoqs.webp',NULL,'2026-07-10 12:32:47'),('A014','Obito','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/Obito_rwjgli.jpg',NULL,'2026-07-10 12:32:47'),('A015','Tăng Phúc','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580513/tangphuc_cridxb.jpg',NULL,'2026-07-10 12:32:47'),('A016','JustaTee','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/justatee_sencbr.jpg',NULL,'2026-07-10 12:32:47'),('A017','VSTRA','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580515/vstra_xpzutq.jpg',NULL,'2026-07-10 12:32:47'),('A018','Karik','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580517/karik_kbgb1d.jpg',NULL,'2026-07-10 12:32:47'),('A019','Sơn Tùng MTP','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/sontung_eqx9ks.webp',NULL,'2026-07-10 12:32:47'),('A020','Maiquiin','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/maiquinn_spnwfk.jpg',NULL,'2026-07-10 12:32:47'),('A021','Muội','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580518/muoi_qvdtke.jpg',NULL,'2026-07-10 12:32:47'),('A022','Yeolan','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/yeolan_ev9fki.webp',NULL,'2026-07-10 12:32:47'),('A023','Ánh Sáng AZA','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580512/aza_flu62w.webp',NULL,'2026-07-10 12:32:47'),('A024','Đào tử A1J','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580515/daotu_zravwi.png',NULL,'2026-07-10 12:32:47');
/*!40000 ALTER TABLE `artists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS `genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genres` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genres`
--

LOCK TABLES `genres` WRITE;
/*!40000 ALTER TABLE `genres` DISABLE KEYS */;
INSERT INTO `genres` VALUES ('G002','Ballad'),('G005','HipHop'),('G003','Lofi Chill'),('G001','Pop'),('G004','R&B');
/*!40000 ALTER TABLE `genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interaction_logs`
--

DROP TABLE IF EXISTS `interaction_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interaction_logs` (
  `id` varchar(50) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `song_id` varchar(50) NOT NULL,
  `listen_duration` int DEFAULT '0',
  `is_liked` tinyint(1) DEFAULT '0',
  `is_skipped` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_il_users` (`user_id`),
  KEY `fk_il_songs` (`song_id`),
  CONSTRAINT `fk_il_songs` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_il_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interaction_logs`
--

LOCK TABLES `interaction_logs` WRITE;
/*!40000 ALTER TABLE `interaction_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `interaction_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_songs`
--

DROP TABLE IF EXISTS `playlist_songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist_songs` (
  `playlist_id` varchar(50) NOT NULL,
  `song_id` varchar(50) NOT NULL,
  `added_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`playlist_id`,`song_id`),
  KEY `fk_ps_songs` (`song_id`),
  CONSTRAINT `fk_ps_playlists` FOREIGN KEY (`playlist_id`) REFERENCES `playlists` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ps_songs` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_songs`
--

LOCK TABLES `playlist_songs` WRITE;
/*!40000 ALTER TABLE `playlist_songs` DISABLE KEYS */;
INSERT INTO `playlist_songs` VALUES ('P003','S001','2026-07-11 03:54:29'),('P003','S002','2026-07-11 03:54:27'),('P004','S002','2026-07-11 04:06:33');
/*!40000 ALTER TABLE `playlist_songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlists`
--

DROP TABLE IF EXISTS `playlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlists` (
  `id` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `is_private` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fk_playlists_users` (`user_id`),
  CONSTRAINT `fk_playlists_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlists`
--

LOCK TABLES `playlists` WRITE;
/*!40000 ALTER TABLE `playlists` DISABLE KEYS */;
INSERT INTO `playlists` VALUES ('P001','dsa','39a5870a-6444-42f0-b885-c72a4cb380e2',NULL,0,'2026-07-10 17:13:38'),('P002','cx','39a5870a-6444-42f0-b885-c72a4cb380e2',NULL,0,'2026-07-10 17:14:51'),('P003','dsa','6df27a95-534a-49f3-9a7d-0402aaa7f7df',NULL,0,'2026-07-11 03:54:17'),('P004','bai thich','0e989155-29a0-4ddb-a065-6bde602ae57e',NULL,0,'2026-07-11 04:06:33');
/*!40000 ALTER TABLE `playlists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song_artists`
--

DROP TABLE IF EXISTS `song_artists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song_artists` (
  `song_id` varchar(50) NOT NULL,
  `artist_id` varchar(50) NOT NULL,
  `is_main_artist` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`song_id`,`artist_id`),
  KEY `fk_sa_artists` (`artist_id`),
  CONSTRAINT `fk_sa_artists` FOREIGN KEY (`artist_id`) REFERENCES `artists` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sa_songs` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song_artists`
--

LOCK TABLES `song_artists` WRITE;
/*!40000 ALTER TABLE `song_artists` DISABLE KEYS */;
INSERT INTO `song_artists` VALUES ('S001','A001',1),('S002','A018',1),('S003','A003',1),('S004','A004',1),('S005','A004',1),('S006','A005',1),('S007','A006',1),('S008','A007',1),('S009','A008',1),('S009','A018',0),('S010','A009',1),('S010','A020',1),('S010','A021',1),('S010','A022',1),('S010','A024',1),('S011','A010',1),('S012','A011',1),('S013','A019',1),('S014','A013',1),('S015','A014',1),('S016','A015',1),('S017','A023',1),('S018','A013',0),('S018','A016',1),('S019','A017',1);
/*!40000 ALTER TABLE `song_artists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `song_genres`
--

DROP TABLE IF EXISTS `song_genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song_genres` (
  `song_id` varchar(50) NOT NULL,
  `genre_id` varchar(50) NOT NULL,
  PRIMARY KEY (`song_id`,`genre_id`),
  KEY `fk_sg_genres` (`genre_id`),
  CONSTRAINT `fk_sg_genres` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_sg_songs` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song_genres`
--

LOCK TABLES `song_genres` WRITE;
/*!40000 ALTER TABLE `song_genres` DISABLE KEYS */;
INSERT INTO `song_genres` VALUES ('S001','G001'),('S006','G001'),('S007','G001'),('S008','G001'),('S010','G001'),('S011','G001'),('S013','G001'),('S014','G001'),('S017','G001'),('S018','G001'),('S007','G002'),('S016','G002'),('S003','G003'),('S006','G003'),('S017','G003'),('S019','G003'),('S001','G004'),('S003','G004'),('S008','G004'),('S012','G004'),('S014','G004'),('S015','G004'),('S018','G004'),('S002','G005'),('S004','G005'),('S005','G005'),('S009','G005'),('S012','G005'),('S013','G005');
/*!40000 ALTER TABLE `song_genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `songs`
--

DROP TABLE IF EXISTS `songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `songs` (
  `id` varchar(50) NOT NULL,
  `title` varchar(255) NOT NULL,
  `file_url` varchar(500) NOT NULL,
  `cover_url` varchar(500) DEFAULT NULL,
  `duration` int DEFAULT '0',
  `views` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `songs`
--

LOCK TABLES `songs` WRITE;
/*!40000 ALTER TABLE `songs` DISABLE KEYS */;
INSERT INTO `songs` VALUES ('S001','Em','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551997/07._Em_-_Binz_feat._SOOBIN___G%E1%BA%B7p_L%E1%BA%A1i_Album_mna9b9.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811304/em_oj4oun.webp',295,150,'2026-07-10 12:32:47'),('S002','Anh Không Đòi Quà','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551912/Anh_Kh%C3%B4ng_%C4%90%C3%B2i_Qu%C3%A0_-_OnlyC_ft_Karik___Official_Music_Video_w8mrcf.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1783740228/music_app/covers/cop9m6irbrlp3uwpdozs.jpg',230,90,'2026-07-10 12:32:47'),('S003','ĐỢI','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551845/%C4%90%E1%BB%A2I_-_52Hz_prod._RIO___Official_Lyric_Video_wxhyp5.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1783740272/music_app/covers/jre2ibovjbuzqbvrjjxw.jpg',220,310,'2026-07-10 12:32:47'),('S004','Người Im Lặng','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551953/HIEUTHUHAI_-_Ng%C6%B0%E1%BB%9Di_Im_L%E1%BA%B7ng_G%E1%BA%B7p_Ng%C6%B0%E1%BB%9Di_Hay_N%C3%B3i_prod._by_Kewtiie_l_Official_Music_Video_vg2ije.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1783740611/music_app/covers/flaltho2vdtpfiqqy4fq.webp',180,500,'2026-07-10 12:32:47'),('S005','TRÌNH','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551949/HIEUTHUHAI_-_TR%C3%8CNH_prod._by_Kewtiie_vbgx5g.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1783740645/music_app/covers/yteanxjiiqxanmgk8qis.webp',195,620,'2026-07-10 12:32:47'),('S006','Hóa Ra','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551949/HIEUTHUHAI_-_TR%C3%8CNH_prod._by_Kewtiie_vbgx5g.mp3','',240,430,'2026-07-10 12:32:47'),('S007','NGƯỜI ĐẦU TIÊN','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552080/JUKY_SAN_-_NG%C6%AF%E1%BB%9CI_%C4%90%E1%BA%A6U_TI%C3%8AN_B%E1%BA%A2N_%C4%90%E1%BA%A6U_TI%C3%8AN___ALBUM_%C4%90%E1%BA%AAM_T%C3%8CNH_sqdrk5.mp3','',210,210,'2026-07-10 12:32:47'),('S008','Không Thời Gian','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552084/Kh%C3%B4ng_Th%E1%BB%9Di_Gian_-_D%C6%B0%C6%A1ng_Domic_Official_Visualizer_une3jy.mp3','',235,890,'2026-07-10 12:32:47'),('S009','Khu Tao Sống','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552168/Khu_Tao_Song_-_Wowy_Karik_OFFICIAL_VIDEO_HD_njzksy.mp3','',260,105,'2026-07-10 12:32:47'),('S010','MASHUP ROCK THIỆP HỒNG BẰNG LĂNG','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552214/MASHUP_ROCK_THI%E1%BB%86P_H%E1%BB%92NG___T%C3%93C_TI%C3%8AN_MAIQUINN_MU%E1%BB%98II_YEOLAN_%C4%90%C3%80O_T%E1%BB%AC_A1J_x_DTAP___LSX_2025_irb3k5.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811305/rock_thiep_hong_cfep8l.webp',300,75,'2026-07-10 12:32:47'),('S011','TRÊN TÌNH BẠN DƯỚI TÌNH YÊU','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552278/MIN_-_TR%C3%8AN_T%C3%8CNH_B%E1%BA%A0N_D%C6%AF%E1%BB%9AI_T%C3%8CNH_Y%C3%8AU___OFFICIAL_MUSIC_VIDEO_ldaxdm.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811305/trenbanduoiyeu_h7hvfr.jpg',200,1200,'2026-07-10 12:32:47'),('S012','TUYỂN BẠN GÁI','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552228/OgeNus_-_TUY%E1%BB%82N_B%E1%BA%A0N_G%C3%81I_ft_Dangrangto_Prod._Machiot___Official_MV_naiftw.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811311/tuyen_ban_gai_t1vudw.png',190,340,'2026-07-10 12:32:47'),('S013','Come My Way','https://res.cloudinary.com/dh74ijavf/video/upload/v1782551886/SON_TUNG_M-TP_x_TYGA___COME_MY_WAY___OFFICIAL_MUSIC_VIDEO_ffbdr2.mp3','',195,180,'2026-07-10 12:32:47'),('S014','VỖ TAY','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552337/PH%C6%AF%C6%A0NG_LY_-_V%E1%BB%96_TAY_EM_TH%C3%82N_Y%C3%8AU_EM_GI%E1%BB%8EI_QU%C3%81_%C4%90I_VERSION___OFFICIAL_MUSIC_VIDEO_unmtmo.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811308/votay_fo0jb2.jpg',185,950,'2026-07-10 12:32:47'),('S015','Simple Love','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552313/Simple_Love_-_Obito_x_Seachains_x_Davis_x_Lena_Official_MV_m2pdbg.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811305/simplelove_ga26yi.jpg',210,1400,'2026-07-10 12:32:47'),('S016','PHỐ XA','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552018/T%C4%82NG_PH%C3%9AC___PH%E1%BB%90_XA_L%C3%AA_Qu%E1%BB%91c_Th%E1%BA%AFng___Live_in_M%C3%82Y_LANG_THANG_22.11.2020__%C4%90%C3%80_L%E1%BA%A0T_ynlen1.mp3','',250,260,'2026-07-10 12:32:47'),('S017','tâm trí lang thang','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552081/t%C3%A2m_tr%C3%AD_lang_thang_-_%C3%81nh_S%C3%A1ng_AZA_ft._Negav_Official_Visualizer_qaahzh.mp3','',225,510,'2026-07-10 12:32:47'),('S018','THẰNG ĐIÊN','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552205/TH%E1%BA%B0NG_%C4%90I%C3%8AN___JUSTATEE_x_PH%C6%AF%C6%A0NG_LY___OFFICIAL_MV_x6nnms.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1783740157/music_app/covers/pvo949erj5njejkngbzp.jpg',245,3200,'2026-07-10 12:32:47'),('S019','Ai Ngoài Anh','https://res.cloudinary.com/dh74ijavf/video/upload/v1782552222/VSTRA_-_Ai_Ngo%C3%A0i_Anh_Official_Audio_jxhtje.mp3','https://res.cloudinary.com/dh74ijavf/image/upload/v1782811303/ai_ngoai_anh_yb3ndi.webp',215,170,'2026-07-10 12:32:47');
/*!40000 ALTER TABLE `songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_followed_artists`
--

DROP TABLE IF EXISTS `user_followed_artists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_followed_artists` (
  `user_id` varchar(255) NOT NULL,
  `artist_id` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`,`artist_id`),
  KEY `artist_id` (`artist_id`),
  CONSTRAINT `user_followed_artists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_followed_artists_ibfk_2` FOREIGN KEY (`artist_id`) REFERENCES `artists` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_followed_artists`
--

LOCK TABLES `user_followed_artists` WRITE;
/*!40000 ALTER TABLE `user_followed_artists` DISABLE KEYS */;
INSERT INTO `user_followed_artists` VALUES ('6df27a95-534a-49f3-9a7d-0402aaa7f7df','A004'),('39a5870a-6444-42f0-b885-c72a4cb380e2','A005'),('0e989155-29a0-4ddb-a065-6bde602ae57e','A011'),('39a5870a-6444-42f0-b885-c72a4cb380e2','A011');
/*!40000 ALTER TABLE `user_followed_artists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_genre_preferences`
--

DROP TABLE IF EXISTS `user_genre_preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_genre_preferences` (
  `user_id` varchar(255) NOT NULL,
  `genre_id` varchar(50) NOT NULL,
  `preference_score` decimal(5,2) DEFAULT '0.00',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`genre_id`),
  KEY `fk_ugp_genres` (`genre_id`),
  CONSTRAINT `fk_ugp_genres` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ugp_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_genre_preferences`
--

LOCK TABLES `user_genre_preferences` WRITE;
/*!40000 ALTER TABLE `user_genre_preferences` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_genre_preferences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_liked_songs`
--

DROP TABLE IF EXISTS `user_liked_songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_liked_songs` (
  `user_id` varchar(255) NOT NULL,
  `song_id` varchar(50) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`song_id`),
  KEY `song_id` (`song_id`),
  CONSTRAINT `user_liked_songs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_liked_songs_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_liked_songs`
--

LOCK TABLES `user_liked_songs` WRITE;
/*!40000 ALTER TABLE `user_liked_songs` DISABLE KEYS */;
INSERT INTO `user_liked_songs` VALUES ('0e989155-29a0-4ddb-a065-6bde602ae57e','S001','2026-07-11 04:06:22'),('0e989155-29a0-4ddb-a065-6bde602ae57e','S012','2026-07-11 04:06:49'),('39a5870a-6444-42f0-b885-c72a4cb380e2','S006','2026-07-10 17:13:44'),('39a5870a-6444-42f0-b885-c72a4cb380e2','S012','2026-07-10 17:15:05'),('6df27a95-534a-49f3-9a7d-0402aaa7f7df','S001','2026-07-11 03:46:05'),('6df27a95-534a-49f3-9a7d-0402aaa7f7df','S002','2026-07-11 03:46:06'),('6df27a95-534a-49f3-9a7d-0402aaa7f7df','S003','2026-07-11 03:46:06'),('6df27a95-534a-49f3-9a7d-0402aaa7f7df','S004','2026-07-11 03:46:07'),('6df27a95-534a-49f3-9a7d-0402aaa7f7df','S005','2026-07-11 04:01:33');
/*!40000 ALTER TABLE `user_liked_songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_streaks`
--

DROP TABLE IF EXISTS `user_streaks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_streaks` (
  `user_id` varchar(255) NOT NULL,
  `current_streak` int DEFAULT '0',
  `max_streak` int DEFAULT '0',
  `last_completed_date` date DEFAULT NULL,
  `today_listening_time` int DEFAULT '0',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_streaks`
--

LOCK TABLES `user_streaks` WRITE;
/*!40000 ALTER TABLE `user_streaks` DISABLE KEYS */;
INSERT INTO `user_streaks` VALUES ('0e989155-29a0-4ddb-a065-6bde602ae57e',0,0,NULL,360,'2026-07-11 13:30:08');
/*!40000 ALTER TABLE `user_streaks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(50) DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('0b379856-48b2-4ead-90c2-2cc047072112','khanhne1703@gmail.com','Female',NULL,'2026-07-10 16:32:46'),('0e989155-29a0-4ddb-a065-6bde602ae57e','titisang1703@gmail.com','Female',NULL,'2026-07-10 16:31:54'),('39a5870a-6444-42f0-b885-c72a4cb380e2','khanhne1703@gmail.com','Male','content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F25/ORIGINAL/NONE/548644413','2026-07-10 17:06:04'),('6df27a95-534a-49f3-9a7d-0402aaa7f7df','khanhdz1703@gmail.com','Male',NULL,'2026-07-11 03:01:54');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-11 21:51:26
