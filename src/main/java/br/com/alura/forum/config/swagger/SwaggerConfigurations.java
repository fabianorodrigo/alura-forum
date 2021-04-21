package br.com.alura.forum.config.swagger;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.alura.forum.modelo.Usuario;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfigurations {

    @Bean
    public Docket forumApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                // identifica a partir de qual pacote que se inicia a geração da documetnação
                .apis(RequestHandlerSelectors.basePackage("br.com.alura.forum"))
                // filtra os PATHS
                .paths(PathSelectors.ant("/**")).build()
                // ignora parâmetros de determinados tipos
                .ignoredParameterTypes(Usuario.class)
                // adiciona parâmetros comuns a todos os endpoints (globais)
                .globalOperationParameters(
                        Arrays.asList(new ParameterBuilder().name("Authorization").description("Header para Token JWT")
                                .modelRef(new ModelRef("string")).parameterType("header").required(false).build()));
    }
}
