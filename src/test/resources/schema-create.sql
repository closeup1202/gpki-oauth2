create table tb_gpls_api_call_logs
(
    id               bigserial constraint api_call_logs_pkey primary key,
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

create table tb_gpls_client
(
    uuid                       varchar(255) not null primary key,
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