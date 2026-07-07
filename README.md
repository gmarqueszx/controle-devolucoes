# Conectsol — Controle de Sobras

Sistema de gestão de instalações solares, equipes de campo e alertas de qualidade. Migração do controle antes mantido na planilha `RELATORIO_DE_SOBRAS_V5` para uma aplicação web (API Spring Boot + frontend Angular).

## Estrutura

```
controle_sobras/           # backend (Spring Boot)
└── conectsol-frontend/    # frontend (Angular)
```

## Backend

Stack: Java 21, Spring Boot 3.3, Maven, Spring Data JPA, PostgreSQL, Spring Security + JWT, Flyway, Springdoc OpenAPI.

### Pré-requisitos
- Java 21
- PostgreSQL 16 rodando localmente (usuário `postgres`, senha `postgres` por padrão — ajustável via `DB_PASSWORD`)
- Maven (ou use o Maven embutido no IntelliJ)

### Rodando localmente

```bash
createdb conectsol
cd controle_sobras
mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- Health check: http://localhost:8080/actuator/health

A senha do banco e o segredo JWT podem ser sobrescritos pelas variáveis de ambiente `DB_PASSWORD` e `JWT_SECRET` (veja `src/main/resources/application.yml`).

### Testes

```bash
mvn test
```

### Criando o primeiro usuário

O cadastro de usuários não possui endpoint próprio (fora do escopo inicial); insira o primeiro usuário ADMIN diretamente no banco após rodar as migrations, com a senha em BCrypt:

```sql
INSERT INTO usuarios (nome, email, senha_hash, perfil)
VALUES ('Admin', 'admin@conectsol.com', '<hash-bcrypt-da-senha>', 'ADMIN');
```

## Frontend

Stack: Angular 17 (standalone components), Angular Material, ngx-charts, RxJS, TypeScript estrito.

### Pré-requisitos
- Node.js 20+ e npm

### Rodando localmente

```bash
cd conectsol-frontend
npm install
npx ng serve
```

App disponível em http://localhost:4200. A URL da API é configurada em `src/environments/environment.ts` / `environment.development.ts` (`http://localhost:8080/api` por padrão).

### Build de produção

```bash
npx ng build
```

## Ordem recomendada para rodar o sistema

1. Subir o PostgreSQL e criar o banco `conectsol`.
2. Rodar o backend (`mvn spring-boot:run`) — o Flyway cria as tabelas automaticamente.
3. Inserir um usuário ADMIN no banco (veja acima).
4. Rodar o frontend (`npx ng serve`).
5. Acessar http://localhost:4200/login.
