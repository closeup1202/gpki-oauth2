-- (1) test client
insert into TB_GPLS_CLIENT (CLIENT_ID_ISSU_DE, CLIENT_SECRET_KEY_EXPRY_DE, AUTHRT_GRANT_TY, CLIENT_AUTHRT_MTHD,
                            LGT_AFTR_REDIRECT_URI, REDIRECT_URI, SCOPE, CLIENT_STNG, TOKEN_STNG, API_KEY_STTS_CD,
                            CLIENT_ID, CLIENT_NM, CLIENT_SECRET_KEY, TEST_KEY_YN, UUID)
values (null, null, null, null, 'http://localhost:5173',
        'http://localhost:5173/oauth2/callback', 'openid', null, null, '01', 'testclient',
        'testWorks', 'testsecretkey', 'Y', 'db7c43ff-c5ba-45ba-a3f8-9e80c6408837');


-- (2) democlient
insert into TB_GPLS_CLIENT (CLIENT_ID_ISSU_DE, CLIENT_SECRET_KEY_EXPRY_DE, AUTHRT_GRANT_TY, CLIENT_AUTHRT_MTHD,
                            LGT_AFTR_REDIRECT_URI, REDIRECT_URI, SCOPE, CLIENT_STNG, TOKEN_STNG, API_KEY_STTS_CD,
                            CLIENT_ID, CLIENT_NM, CLIENT_SECRET_KEY, TEST_KEY_YN, UUID)
values (null, null, null, null, 'https://saas.go.kr/test-gpki-client',
        'https://saas.go.kr/test-gpki-client/oauth2/callback', 'openid', null, null, '01', 'democlient',
        'demoWorks', 'demosecretkey', 'Y', 'db7c43ff-c5ba-45ba-a3f8-9e80c6408837');
