-- =========================================================================
-- 2. NẠP DỮ LIỆU MẪU CHUẨN XÁC THEO KIẾN TRÚC ĐA NGHỆ SĨ & ĐA THỂ LOẠI
-- =========================================================================

-- Nạp Thể loại tách rời R&B (G004) và HipHop (G005)
INSERT INTO genres (id, name) VALUES 
('G001', 'Pop'), 
('G002', 'Ballad'), 
('G003', 'Lofi Chill'), 
('G004', 'R&B'), 
('G005', 'HipHop');

-- Nạp Nghệ sĩ (Thêm Karik A018 và Seachains A019 để làm đồng tác giả)
INSERT INTO artists (id, name, avatar_url) VALUES 
('A001', 'Binz', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782580514/binz_frek6h.webp'), 
('A003', '52Hz','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580511/52hz_r2zjez.webp'), 
('A004', 'HIEUTHUHAI','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/ht2_ypqbc1.jpg'), 
('A005', 'GREY D','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580517/greyD_oghkei.jpg'), 
('A006', 'Juky San','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580518/jukysan_rhzvis.jpg'), 
('A007', 'Dương Domic','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/duongdomic_rplmcw.jpg'), 
('A008', 'Wowy','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580515/wowy_au4mtt.jpg'), 
('A009', 'Tóc Tiên','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580512/toctien_xrfpij.webp'), 
('A010', 'MIN','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580518/min_prxc8s.webp'), 
('A011', 'OgeNus','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580520/ogenus_pythgn.jpg'), 
('A012', 'Phùng Khánh Linh','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/phungklinh_jzt9od.webp'), 
('A013', 'Phương Ly','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/phuongly_ewgoqs.webp'), 
('A014', 'Obito','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/Obito_rwjgli.jpg'), 
('A015', 'Tăng Phúc','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580513/tangphuc_cridxb.jpg'), 
('A016', 'JustaTee','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/justatee_sencbr.jpg'), 
('A017', 'VSTRA','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580515/vstra_xpzutq.jpg'), 
('A018', 'Karik','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580517/karik_kbgb1d.jpg'), 
('A019','Sơn Tùng MTP','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/sontung_eqx9ks.webp'),
('A020','Maiquiin','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580519/maiquinn_spnwfk.jpg'),
('A021','Muội','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580518/muoi_qvdtke.jpg'),
('A022','Yeolan','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580516/yeolan_ev9fki.webp'),
('A023','Ánh Sáng AZA','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580512/aza_flu62w.webp'),
('A024','Đào tử A1J','https://res.cloudinary.com/dh74ijavf/image/upload/v1782580515/daotu_zravwi.png');

USE music_app_db;

INSERT INTO songs (id, title, file_url, cover_url, duration, views) VALUES 
('S001', 'Em', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551997/07._Em_-_Binz_feat._SOOBIN___G%E1%BA%B7p_L%E1%BA%A1i_Album_mna9b9.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811304/em_oj4oun.webp', 295, 150),
('S002', 'Anh Không Đòi Quà', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551912/Anh_Kh%C3%B4ng_%C4%90%C3%B2i_Qu%C3%A0_-_OnlyC_ft_Karik___Official_Music_Video_w8mrcf.mp3', '', 230, 90),
('S003', 'ĐỢI', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551845/%C4%90%E1%BB%A2I_-_52Hz_prod._RIO___Official_Lyric_Video_wxhyp5.mp3', '', 220, 310),
('S004', 'Người Im Lặng', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551953/HIEUTHUHAI_-_Ng%C6%B0%E1%BB%9Di_Im_L%E1%BA%B7ng_G%E1%BA%B7p_Ng%C6%B0%E1%BB%9Di_Hay_N%C3%B3i_prod._by_Kewtiie_l_Official_Music_Video_vg2ije.mp3', '', 180, 500),
('S005', 'TRÌNH', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551949/HIEUTHUHAI_-_TR%C3%8CNH_prod._by_Kewtiie_vbgx5g.mp3', '', 195, 620),
('S006', 'Hóa Ra', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551949/HIEUTHUHAI_-_TR%C3%8CNH_prod._by_Kewtiie_vbgx5g.mp3', '', 240, 430),
('S007', 'NGƯỜI ĐẦU TIÊN', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552080/JUKY_SAN_-_NG%C6%AF%E1%BB%9CI_%C4%90%E1%BA%A6U_TI%C3%8AN_B%E1%BA%A2N_%C4%90%E1%BA%A6U_TI%C3%8AN___ALBUM_%C4%90%E1%BA%AAM_T%C3%8CNH_sqdrk5.mp3', '', 210, 210),
('S008', 'Không Thời Gian', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552084/Kh%C3%B4ng_Th%E1%BB%9Di_Gian_-_D%C6%B0%C6%A1ng_Domic_Official_Visualizer_une3jy.mp3', '', 235, 890),
('S009', 'Khu Tao Sống', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552168/Khu_Tao_Song_-_Wowy_Karik_OFFICIAL_VIDEO_HD_njzksy.mp3', '', 260, 105),
('S010', 'MASHUP ROCK THIỆP HỒNG BẰNG LĂNG', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552214/MASHUP_ROCK_THI%E1%BB%86P_H%E1%BB%92NG___T%C3%93C_TI%C3%8AN_MAIQUINN_MU%E1%BB%98II_YEOLAN_%C4%90%C3%80O_T%E1%BB%AC_A1J_x_DTAP___LSX_2025_irb3k5.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811305/rock_thiep_hong_cfep8l.webp', 300, 75),
('S011', 'TRÊN TÌNH BẠN DƯỚI TÌNH YÊU', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552278/MIN_-_TR%C3%8AN_T%C3%8CNH_B%E1%BA%A0N_D%C6%AF%E1%BB%9AI_T%C3%8CNH_Y%C3%8AU___OFFICIAL_MUSIC_VIDEO_ldaxdm.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811305/trenbanduoiyeu_h7hvfr.jpg', 200, 1200),
('S012', 'TUYỂN BẠN GÁI', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552228/OgeNus_-_TUY%E1%BB%82N_B%E1%BA%A0N_G%C3%81I_ft_Dangrangto_Prod._Machiot___Official_MV_naiftw.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811311/tuyen_ban_gai_t1vudw.png', 190, 340),
('S013', 'Come My Way', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782551886/SON_TUNG_M-TP_x_TYGA___COME_MY_WAY___OFFICIAL_MUSIC_VIDEO_ffbdr2.mp3', '', 195, 180),
('S014', 'VỖ TAY', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552337/PH%C6%AF%C6%A0NG_LY_-_V%E1%BB%96_TAY_EM_TH%C3%82N_Y%C3%8AU_EM_GI%E1%BB%8EI_QU%C3%81_%C4%90I_VERSION___OFFICIAL_MUSIC_VIDEO_unmtmo.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811308/votay_fo0jb2.jpg', 185, 950),
('S015', 'Simple Love', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552313/Simple_Love_-_Obito_x_Seachains_x_Davis_x_Lena_Official_MV_m2pdbg.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811305/simplelove_ga26yi.jpg', 210, 1400),
('S016', 'PHỐ XA', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552018/T%C4%82NG_PH%C3%9AC___PH%E1%BB%90_XA_L%C3%AA_Qu%E1%BB%91c_Th%E1%BA%AFng___Live_in_M%C3%82Y_LANG_THANG_22.11.2020__%C4%90%C3%80_L%E1%BA%A0T_ynlen1.mp3', '', 250, 260),
('S017', 'tâm trí lang thang', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552081/t%C3%A2m_tr%C3%AD_lang_thang_-_%C3%81nh_S%C3%A1ng_AZA_ft._Negav_Official_Visualizer_qaahzh.mp3', '', 225, 510),
('S018', 'THẰNG ĐIÊN', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552205/TH%E1%BA%B0NG_%C4%90I%C3%8AN___JUSTATEE_x_PH%C6%AF%C6%A0NG_LY___OFFICIAL_MV_x6nnms.mp3', '', 245, 3200),
('S019', 'Ai Ngoài Anh', 'https://res.cloudinary.com/dh74ijavf/video/upload/v1782552222/VSTRA_-_Ai_Ngo%C3%A0i_Anh_Official_Audio_jxhtje.mp3', 'https://res.cloudinary.com/dh74ijavf/image/upload/v1782811303/ai_ngoai_anh_yb3ndi.webp', 215, 170);

INSERT INTO song_artists (song_id, artist_id, is_main_artist) VALUES 
('S001', 'A001', 1), 
('S002', 'A018', 1), 
('S003', 'A003', 1),
('S004', 'A004', 1), ('S005', 'A004', 1), -- HIEUTHUHAI
('S006', 'A005', 1), -- hóa ra...: GREY D
('S007', 'A006', 1), -- NGƯỜI ĐẦU TIÊN: Juky San
('S008', 'A007', 1), -- Không Thời Gian: Dương Domic
('S009', 'A008', 1), ('S009', 'A018', 0), -- Khu Tao Sống: Wowy ft. Karik
('S010', 'A009', 1), ('S010','A020',1), ('S010','A021',1), ('S010','A022',1), ('S010','A024',1), -- Mashup: Làn Sóng Xanh
('S011', 'A010', 1), -- TRÊN TÌNH BẠN...: MIN
('S012', 'A011', 1), -- TUYỂN BẠN GÁI: OgeNus
('S013', 'A012', 1), -- ANH SẼ ĐẾN TRONG GIẤC MƠ: Phùng Khánh Linh
('S014', 'A013', 1), -- VỖ TAY: Phương Ly
('S015', 'A014', 1),
('S016', 'A015', 1), -- PHỐ XA: Tăng Phúc
('S017', 'A023', 1), -- tâm trí lang thang: Ánh Sáng AZA
('S018', 'A016', 1), ('S018', 'A013', 0), -- THẰNG ĐIÊN: JustaTee x Phương Ly
('S019', 'A017', 1); -- Ai Ngoài Anh: VSTRA


-- Nạp dữ liệu vào bảng song_genres
INSERT INTO song_genres (song_id, genre_id) VALUES 
('S001', 'G001'), ('S001', 'G004'), -- Em: Pop + R&B
('S002', 'G005'),                 -- Anh Không Đòi Quà: HipHop
('S003', 'G003'), ('S003', 'G004'), -- ĐỢI: Lofi Chill + R&B
('S004', 'G005'),                 -- Người Im Lặng: HipHop
('S005', 'G005'),                 -- TRÌNH: HipHop
('S006', 'G001'), ('S006', 'G003'), -- Hóa Ra: Pop + Lofi Chill
('S007', 'G001'), ('S007', 'G002'), -- NGƯỜI ĐẦU TIÊN: Pop + Ballad
('S008', 'G001'), ('S008', 'G004'), -- Không Thời Gian: Pop + R&B
('S009', 'G005'),                 -- Khu Tao Sống: HipHop
('S010', 'G001'),                 -- MASHUP ROCK THIỆP HỒNG BẰNG LĂNG: Pop
('S011', 'G001'),                 -- TRÊN TÌNH BẠN DƯỚI TÌNH YÊU: Pop
('S012', 'G004'), ('S012', 'G005'), -- TUYỂN BẠN GÁI: R&B + HipHop
('S013', 'G001'), ('S013', 'G005'), -- Come My Way: Pop + HipHop
('S014', 'G001'), ('S014', 'G004'), -- VỖ TAY: Pop + R&B
('S015', 'G004'),                 -- Simple Love: R&B
('S016', 'G002'),                 -- PHỐ XA: Ballad
('S017', 'G001'), ('S017', 'G003'), -- tâm trí lang thang: Pop + Lofi Chill
('S018', 'G001'), ('S018', 'G004'), -- THẰNG ĐIÊN: Pop + R&B
('S019', 'G003');                 -- Ai Ngoài Anh: Lofi Chill 