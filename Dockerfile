# Dockerfile - Tudo acontece dentro do container!
# NÃO precisa de Maven no seu PC
# NÃO precisa de JDK no seu PC

# ============================================
# ESTÁGIO 1: BUILD (compila a aplicação)
# ============================================
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copia os arquivos de configuração do Maven
COPY pom.xml .

# Baixa todas as dependências (isso acontece dentro do container!)
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila e gera o JAR (tudo dentro do container!)
RUN mvn clean package -DskipTests

# ============================================
# ESTÁGIO 2: RUNTIME (executa a aplicação)
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o JAR que foi gerado no estágio anterior
COPY --from=builder /build/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Roda a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]