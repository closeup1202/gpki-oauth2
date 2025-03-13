package kr.go.saas.gpkiauth.history.entity;

import jakarta.persistence.*;
import kr.go.saas.gpkiauth.history.model.ApiCallLogModel;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@ToString
@Table(name = "TB_GPLS_API_CALL_LOGS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiCallLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_srvc_id")
    private String linkServiceId;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "http_mthd")
    private String httpMethod;

    @Column(name = "rqst_tm")
    private Date requestAt;

    private String path;

    @Column(name = "elapse_hr")
    private Long elapseHour;

    private Long cnt = 1L;

    @Column(name = "stts_cd")
    private String statusCode;

    @Column(name = "rspns_mssage")
    private String responseMessage;

    @Column(name = "error_dtl_mssage")
    private String errorMessage;

    @Builder
    public ApiCallLog(String linkServiceId, String traceId, String httpMethod, Date requestAt, String path,
                      Long elapseHour, String statusCode, String responseMessage, Long cnt, String errorMessage) {
        this.linkServiceId = linkServiceId;
        this.traceId = traceId;
        this.httpMethod = httpMethod;
        this.requestAt = requestAt;
        this.path = path;
        this.elapseHour = elapseHour;
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.cnt = cnt;
        this.errorMessage = errorMessage;
    }

    public static ApiCallLog from(ApiCallLogModel model) {
        return ApiCallLog.builder()
                .linkServiceId(model.linkServiceId())
                .traceId(model.traceId())
                .httpMethod(model.httpMethod())
                .path(model.path())
                .requestAt(model.requestAt())
                .elapseHour(model.elapseHour())
                .statusCode(model.statusCode())
                .cnt(model.cnt())
                .responseMessage(model.responseMessage())
                .errorMessage(model.errorMessage())
                .build();
    }
}