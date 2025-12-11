# TesteTCD

# TesteTCD

Este projeto é a aplicação web usada no teste TCD.

Requisitos
- Java 8+ instalado
- Maven 3.x
- Um servidor de aplicações (por exemplo, Tomcat, Payara) ou contêiner compatível com WAR
- Banco de dados compatível configurado em `persistence.xml`

Como rodar
1. Ajuste a configuração de banco em `src/main/resources/META-INF/persistence.xml` com a URL, usuário e senha do seu banco.
2. Gerar o artefato:

```
mvn clean package
```

3. Faça o deploy do WAR gerado em `target/` no seu servidor de aplicações (ex.: copie `target/*.war` para o `webapps` do Tomcat) e inicie o servidor.

Criação de usuários e admin
- A aplicação armazena senhas como hashes no banco. Para criar um usuário ou administrador é necessário inserir diretamente a hash da senha no campo de senha da tabela de usuários do banco.
- Exemplo (ajuste nomes de tabela/colunas conforme o esquema do seu banco):

```
INSERT INTO usuarios (id, username, senha, papel, ativo) VALUES (1, 'admin', 'HASH_DA_SENHA_AQUI', 'ROLE_ADMIN', true);
```

Gerar a hash da senha
- Para facilitar geramos uma classe temporária no projeto que produz a hash de uma senha em texto puro. Execute essa classe localmente para obter a string da hash e cole-a no banco.
- Procure por classes com nomes como `Hash`, `Hasher`, `PasswordHasher` ou `HashGenerator` no pacote `br.com` do projeto. A classe imprime a hash no console quando executada.
- Exemplo genérico de execução (ajuste o pacote/classe conforme o seu projeto):

```
mvn -q -Dexec.executable=java -Dexec.args="-cp %classpath br.com.seupacote.PasswordHasher minhaSenha" org.codehaus.mojo:exec-maven-plugin:3.0.0:exec
```

Notas importantes
- Inserir diretamente a hash no banco é necessário porque o processo de cadastro da aplicação pode exigir validações/fluxos que não estão disponíveis para criação manual via interface.
- A classe usada para gerar a hash é temporária — remova-a após gerar as hashes de teste/administradores, se desejar.
- Se precisar, eu posso localizar a classe temporária no projeto e fornecer o comando exato para rodá-la.

Localização da classe de hash e como gerar a hash (comando exato)
- A classe utilitária que gera/verifica hashes está em `src/main/java/br/com/sigapar1/util/HashUtil.java` e fornece o método `gerarHash(String)`.
- Para gerar uma hash localmente você pode criar temporariamente uma classe com `main` (exemplo abaixo) e executá-la via Maven.

Exemplo de classe temporária (crie em `src/main/java/br/com/sigapar1/util/HashRunner.java`):

```java
package br.com.sigapar1.util;

public class HashRunner {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Uso: HashRunner <senha>");
			return;
		}
		System.out.println(HashUtil.gerarHash(args[0]));
	}
}
```

Comando para executar a classe temporária via Maven (executa e imprime a hash no console):

```
mvn -q org.codehaus.mojo:exec-maven-plugin:3.0.0:exec -Dexec.mainClass="br.com.sigapar1.util.HashRunner" -Dexec.args="minhaSenha"
```

Depois de gerar as hashes necessárias, remova a classe `HashRunner` se desejar mantê-la temporária.


