package kr.GenAi.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // [수정] 리눅스 서버에는 C드라이브가 없으므로 500 에러 방지를 위해 주석 처리합니다.
        // 나중에 리눅스용 업로드 경로가 생기면 "file:/home/ubuntu/recyq_upload/" 식으로 변경하세요.
        /*
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///C:/recyq_upload/");
        */

    	
    	
        // 정적 리소스 명시 매핑 (이 부분은 유지합니다)
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}