package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpResponse {
    private DataOutputStream dos;
    private HashMap<String, String> headers;
    private int statusCode;
    private String statusMessage;
    private String version;

    // 생성자
    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
        this.headers = new HashMap<>();
        this.version = "HTTP/1.1"; // 기본 HTTP 버전
    }

    // 상태 코드와 메시지를 설정하는 메서드
    public void setStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    // 헤더를 추가하는 메서드
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    // 지정된 경로로 포워드하는 메서드
    public void forward(String path) throws IOException {
        // 성공적인 응답을 위한 상태 코드 설정
        setStatus(StatusCode.OK.getCode(), StatusCode.OK.getPhrase());

        // 응답 헤더를 준비하고 작성
        writeHeaders();

        // 응답 본문(HTML 파일의 내용)을 작성
        byte[] body = Files.readAllBytes(Paths.get("webapp" + path));
        dos.write(body);
        dos.flush();
    }

    // 지정된 경로로 리디렉션하는 메서드
    public void redirect(String path) throws IOException {
        // 리디렉션을 위한 상태 코드 설정
        setStatus(StatusCode.FOUND.getCode(), StatusCode.FOUND.getPhrase());
        addHeader("Location", path); // 리디렉션을 위한 Location 헤더 추가

        // 응답 헤더를 준비하고 작성
        writeHeaders();
    }

    // 출력 스트림에 헤더를 작성하는 메서드
    private void writeHeaders() throws IOException {
        // 상태 라인 작성
        dos.writeBytes(version + " " + statusCode + " " + statusMessage + "\r\n");

        // 각 헤더 작성
        for (HashMap.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        // 헤더 끝
        dos.writeBytes("\r\n");
    }
}