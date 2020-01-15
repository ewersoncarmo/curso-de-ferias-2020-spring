## 5ª edição Curso de Férias 2020 - Construindo APIs REST com Spring

```

```

* ## Estrutura das aulas

  * **Aula 1**
      * [Slides - aula 1](https://docs.google.com/presentation/d/1uTZsgOfTR2DZgLjE344b2IjhYPrlWpnqp0iJ_RQx-lE/edit?usp=sharing)
  * **Aula 2**
      * [Slides - aula 2](https://docs.google.com/presentation/d/1jafO9Yq_v1NGioiRyeDWgvbrUX9LtzgAUoQmGvKCv7Y/edit?usp=sharing)
      * Criação do projeto inicial com [Spring Initializr](https://start.spring.io/)
      * Entendendo o contexto do Spring
        * Inversão de controle
        * Injeção de dependências
      * Criação de beans no contexto do Spring e possíveis problemas
        * Uso da interface **ApplicationRunner**
        * Uso de *annotations* **@Component**, **@Autowired**, **@Primary** e **@Qualifier**
      * Uso de Spring Profiles
      * [Branch - aula 2]
  * **Aula 3**
      * JPA
      * Spring Data
      * H2 console
      * Criação da estrutura do pacote **entity** com as entidades do banco de dados
      * Conexão estabelecida com banco de dados
      * Criação da estrutura do pacote **repository**
      * [Branch - aula 3]
  * **Aula 4**
      * Refatorando entidades com a criação da classe **EntidadeBase**
      * Alterações nos enums **Natureza**, **SituacaoConta** e **TipoLancamento** e seus mapeamentos para não gravar o ordinal dos enums
      * Criação da estrutura do pacote **business**
      * Usando a classe **AppStartupRunner** para alimentar a base de dados do H2 utilizando as classes do pacote **business**
      * [Branch - aula 4]
  * **Aula 5**
      * Criação da estrutura do pacote **dto** com classes de *Response*
      * Criação da estrutura do pacote **service**
      * Criação da estrutura do pacote **controller** com requisições *GET*
      * Anotações do Jackson para formatação do JSON
      * Requisições via Postman
      * [Branch - aula 5]
  * **Aula 6**
      * Criação das classes de *Request* no pacote **dto**
      * Criação das requisições *POST*, *PUT* e *DELETE*
      * Criação de requisições *GET* mais complexas
      * [Branch - aula 6]
  * **Aula 7**
      * Tratamento de códigos de retorno HTTP
      * *Exception handler*
      * Criação de validações na camada **business**
      * Validações nos *requests*
      * [Branch - aula 7]
  * **Aula 8**
      * Testes unitários da camada **business** com *mock*
      * Testes integrados com banco de dados H2
      * [Branch - aula 8]
  * **Aula 9**
      * Revisão
        * *Entity*
        * *Repository*
        * *Business*
        * *Service* e *DTOs*
        * *Controller*
        * *Unit tests*
      * [Branch - aula 9]
  * **Aula 10**
      * Dúvidas
      * [Branch - aula 10]

* ## Projeto
  * ### Especificações técnicas
    * **Linguagem de programação:** Java - jdk1.8.0_152 ou superior
    * **Gerenciador de dependências:** apache-maven-3.0.4
    * **Spring Boot:** 2.2.0
    * **Banco de dados:** H2 database - http://localhost:8080/h2
    * **Testes unitários:** Mockito + JUnit
    * **Testes de integração:** Rest Assured
    * **Swagger**: [swagger.yaml](etc/swagger.yaml)
    * **Postman**: [Digitalbank.postman_collection.json](etc/Digitalbank.postman_collection.json)

  * ### Modelagem
    ![modelagem](etc/modelagem.png)

  * ### Representações

      Os modelos de entrada e saída são representados no formato JSON

      * **Cliente**

        *ClienteRequestDTO*
        ```json
        {
          "nome": "Pedro"
        }
        ```

        *ClienteResponseDTO*
        ```json
        {
          "id": 1,
          "nome": "Pedro"
        }
        ```
      * **Espécie**

        *EspecieRequestDTO*
        ```json
        {
          "descricao": "Cachorro"
        }
        ```

        *EspecieResponseDTO*
        ```json
        {
          "id": 1,
          "descricao": "Cachorro"
        }
        ```
      * **Pet**

        *PetRequestDTO*
        ```json
        {
          "nome": "Rex",
          "dataNascimento": "01/01/2019",
          "idCliente": 1,
          "idEspecie": 1
        }
        ```

        *PetResponseDTO*
        ```json
        {
          "id": 1,
          "nome": "Rex",
          "dataNascimento": "01/01/2019",
          "cliente": {
            "id": 1,
            "nome": "Pedro"
          },
          "especie": {
            "id": 1,
            "descricao": "Cachorro"
          }
        }
        ```
      * **Serviço**

        *ServicoRequestDTO*
        ```json
        {
          "observacao": "Consulta",
          "idTipoServico": 1,
          "valor": 80.00,
          "idPet": 1
        }
        ```

        *ServicoResponseDTO*
        ```json
        {
          "id": 1,
          "observacao": "Consulta",
          "dataHora": "11/01/2019 12:38:17",
          "tipoServico": "Consulta",
          "valor": 80.00,
          "pet": {
            "id": 1,
            "nome": "Rex",
            "dataNascimento": "01/01/2019",
            "cliente": {
              "id": 1,
              "nome": "Pedro"
            },
            "especie": {
              "id": 1,
              "descricao": "Cachorro"
            }
          }
        }
        ```

  * ### Requisições

      * **Cliente**

      Método | URL                                             | Entrada             | Saída
      ------ | ----------------------------------------------- | ------------------- | ------ |
      POST   | http://localhost:8080/api/v1/clientes           | *ClienteRequestDTO* | 201 (Created)
      GET    | http://localhost:8080/api/v1/clientes           |                     | 200 (OK) Lista *ClienteResponseDTO*
      GET    | http://localhost:8080/api/v1/clientes/{id}      |                     | 200 (OK) *ClienteResponseDTO*
      GET    | http://localhost:8080/api/v1/clientes/{id}/pets |                     | 200 (OK) Lista *PetResponseDTO*
      PUT    | http://localhost:8080/api/v1/clientes/{id}      | *ClienteRequestDTO* | 204 (No Content)
      DELETE | http://localhost:8080/api/v1/clientes/{id}      |                     | 204 (No Content)

      * **Espécie**

      Método | URL                                             | Entrada             | Saída
      ------ | ----------------------------------------------- | ------------------- | ------ |
      POST   | http://localhost:8080/api/v1/especies           | *EspecieRequestDTO* | 201 (Created)
      GET    | http://localhost:8080/api/v1/especies           |                     | 200 (OK) Lista *EspecieResponseDTO*
      GET    | http://localhost:8080/api/v1/especies/{id}      |                     | 200 (OK) *EspecieResponseDTO*
      GET    | http://localhost:8080/api/v1/especies/{id}/pets |                     | 200 (OK) Lista *PetResponseDTO*
      PUT    | http://localhost:8080/api/v1/especies/{id}      | *EspecieRequestDTO* | 204 (No Content)
      DELETE | http://localhost:8080/api/v1/especies/{id}      |                     | 204 (No Content)

      * **Pet**

      Método | URL                                             | Entrada          | Saída
      ------ | ----------------------------------------------- | ---------------- | ------ |
      POST   | http://localhost:8080/api/v1/pets               | *PetRequestDTO*  | 201 (Created)
      GET    | http://localhost:8080/api/v1/pets               |                  | 200 (OK) Lista *PetResponseDTO*
      GET    | http://localhost:8080/api/v1/pets/{id}          |                  | 200 (OK) *PetResponseDTO*
      GET    | http://localhost:8080/api/v1/pets/{id}/servicos |                  | 200 (OK) Lista *ServicoResponseDTO*
      PUT    | http://localhost:8080/api/v1/pets/{id}          | *PetRequestDTO*  | 204 (No Content)
      DELETE | http://localhost:8080/api/v1/pets/{id}          |                  | 204 (No Content)

      * **Serviço**

      Método | URL                                             | Entrada             | Saída
      ------ | ----------------------------------------------- | ------------------- | ------ |
      POST   | http://localhost:8080/api/v1/servicos      	   | *ServicoRequestDTO* | 201 (Created)
      GET    | http://localhost:8080/api/v1/servicos      	   |                     | 200 (OK) Lista *ServicoResponseDTO*
      GET    | http://localhost:8080/api/v1/servicos/{id} 	   |                     | 200 (OK) *ServicoResponseDTO*
      GET    | http://localhost:8080/api/v1/servicos/buscaPorData?dataInicial=dd/MM/yyyy&dataFinal=dd/MM/yyyy |                     | 200 (OK) Lista *ServicoResponseDTO*
      PUT    | http://localhost:8080/api/v1/servicos/{id} 	   | *ServicoRequestDTO* | 204 (No Content)
      DELETE | http://localhost:8080/api/v1/servicos/{id} 	   |                     | 204 (No Content)
