create table gpls.tb_gpls_api_call_logs
(
    id               bigserial
        constraint api_call_logs_pkey
            primary key,
    link_srvc_id     varchar(20),
    trace_id         varchar(255),
    http_mthd        varchar(255),
    rqst_tm          timestamp,
    path             varchar(50),
    elapse_hr        bigint,
    cnt              bigint,
    stts_cd          varchar,
    rspns_mssage     varchar(1000),
    error_dtl_mssage varchar(1000)
);

comment on column gpls.tb_gpls_api_call_logs.id is 'ID';
comment on column gpls.tb_gpls_api_call_logs.link_srvc_id is '연계서비스ID';
comment on column gpls.tb_gpls_api_call_logs.trace_id is '추적ID';
comment on column gpls.tb_gpls_api_call_logs.http_mthd is 'HTTP방법';
comment on column gpls.tb_gpls_api_call_logs.rqst_tm is '요청시각';
comment on column gpls.tb_gpls_api_call_logs.path is '경로';
comment on column gpls.tb_gpls_api_call_logs.elapse_hr is '경과시간';
comment on column gpls.tb_gpls_api_call_logs.cnt is '수';
comment on column gpls.tb_gpls_api_call_logs.stts_cd is '상태코드';
comment on column gpls.tb_gpls_api_call_logs.rspns_mssage is '응답메세지';
comment on column gpls.tb_gpls_api_call_logs.error_dtl_mssage is '에러상세메세지';

alter table gpls.tb_gpls_api_call_logs
    owner to root;

---

create table gpls.tb_gpls_client
(
    uuid                       varchar(255) not null
        primary key,
    client_id                  varchar(255),
    client_id_issu_de          timestamp,
    client_nm                  varchar(255),
    client_secret_key          varchar(255),
    client_secret_key_expry_de timestamp,
    client_authrt_mthd         varchar(1000),
    authrt_grant_ty            varchar(1000),
    redirect_uri               varchar(1000),
    lgt_aftr_redirect_uri      varchar(1000),
    scope                      varchar(1000) default 'openid'::character varying,
    client_stng                varchar(2000),
    token_stng                 varchar(2000),
    api_key_stts_cd            varchar(2),
    test_key_yn                varchar(1)
);

alter table gpls.tb_gpls_client
    owner to root;


