# MyContacts

Agenda de contatos com interface gráfica JavaFX, layouts em FXML, estilos CSS e persistência local em SQLite.

## Requisitos

- JDK 21 ou superior
- Apache Maven 3.9 ou superior
- Conexão com a internet na primeira execução para baixar as dependências

O Maven gerencia as dependências do JavaFX, SQLite JDBC e JUnit. Não é necessário baixar ou adicionar esses arquivos manualmente.

## Executar a aplicação

Abra um terminal na pasta que contém o `pom.xml` e execute:

```bash
mvn javafx:run
```

O Maven compila o projeto e abre a interface gráfica do MyContacts. Na primeira execução, ele pode levar mais tempo para baixar as dependências.

### Executar pelo Eclipse

1. Importe a pasta como **Existing Maven Project**: **File → Import → Maven → Existing Maven Projects**.
2. Selecione a pasta onde está o `pom.xml` e conclua a importação.
3. Caso necessário, clique com o botão direito no projeto e escolha **Maven → Update Project...**.
4. Para iniciar a interface, clique com o botão direito no projeto → **Run As → Maven build...**.
5. Informe `javafx:run` em **Goals** e execute.

Use um JDK 21 ou superior na configuração de execução. O modo Maven configura as dependências de runtime do JavaFX.

## Usar a agenda

Na janela principal, é possível:

- Cadastrar contatos pessoais ou comerciais;
- Buscar contatos pelo nome;
- Editar e remover contatos;
- Validar os campos do formulário e o formato do e-mail.

O banco SQLite e a tabela `contatos` são criados automaticamente. Os dados permanecem salvos entre execuções.

## Banco de dados

A aplicação usa o arquivo `mycontacts.db` no diretório de trabalho da execução. Ao iniciar com `mvn javafx:run` na raiz do projeto, normalmente o arquivo é criado na própria pasta do projeto.

O banco é local e não é enviado ao GitHub: `mycontacts.db` e seus arquivos auxiliares estão no `.gitignore`. Uma instalação nova cria um banco vazio na primeira execução. Para preservar os dados, mantenha uma cópia segura do arquivo antes de removê-lo ou mover a aplicação.

É possível abrir `mycontacts.db` com o DB Browser for SQLite e consultar a tabela `contatos`, que contém `id`, `nome`, `telefone`, `email` e `empresa`.

## Executar os testes

Na pasta do projeto, execute:

```bash
mvn test
```

Para compilar a aplicação e executar todos os testes:

```bash
mvn clean verify
```

Os testes automatizados usam JUnit 5. Os testes do DAO criam bancos SQLite em memória, separados do arquivo de dados usado pela aplicação.
