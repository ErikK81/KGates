# KGates

## Resumo

KGates é um plugin para servidores Minecraft Spigot que permite criar portais de teletransporte e warps. Os portais são configurados por uma interface gráfica dentro do jogo e podem ter direção, formato, tamanho, condições de acesso, comandos, partículas, sons e tempo de espera personalizados.

## Funcionalidades

- Criação e edição de portais por menus interativos.
- Portais de mão única (A → B) ou de duas direções (A ↔ B).
- Áreas de ativação em formato de esfera, cilindro ou retângulo.
- Condições de acesso com placeholders e operadores de comparação.
- Partículas e sons de ambiente, entrada, saída e ativação.
- Execução de comandos quando o jogador atravessa um portal.
- Cooldown individual por jogador e portal.
- Sistema de warps com criação, remoção e autocompletar.
- Persistência automática em arquivos YAML.

## Requisitos

- Java 21 ou mais recente.
- Servidor compatível com Spigot API 1.21 (o projeto é compilado com a versão 1.21.8).
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) 2.11.6 ou compatível.
- Maven 3.9 ou mais recente, somente para compilar o projeto.

## Instalação

### Usando um arquivo compilado

1. Instale o PlaceholderAPI na pasta `plugins` do servidor.
2. Coloque o arquivo `kgates-1.0-SNAPSHOT.jar` na mesma pasta.
3. Inicie ou reinicie o servidor.
4. Confirme no console a mensagem `KGates carregado com sucesso!`.

Os dados serão criados em `plugins/KGates/` na primeira inicialização.

### Compilando o código-fonte

Na raiz do projeto, execute:

```sh
mvn clean package
```

O JAR será gerado em `target/kgates-1.0-SNAPSHOT.jar`. As APIs do Spigot e do PlaceholderAPI são dependências fornecidas pelo servidor e não são incluídas dentro do JAR.

## Uso rápido

Para criar um portal:

1. Execute `/kgate create <id>`.
2. No editor, escolha o tipo e defina os pontos A e B clicando nos respectivos itens e depois em um bloco do mundo.
3. Ajuste formato, dimensões, efeitos, condições, comandos e cooldown, se necessário.
4. Clique em **Salvar portal**.
5. Entre na área do ponto A para ser transportado ao ponto B. Em portais de duas direções, o ponto B também leva ao ponto A.

O ID é convertido para letras minúsculas. Para editar um portal posteriormente, use `/kgate edit <id>`; para visualizar os portais existentes, use `/kgate browse`.

## Comandos

### Portais

Todos os comandos abaixo só podem ser executados por jogadores com a permissão `kgates.admin`.

| Comando | Descrição |
| --- | --- |
| `/kgate create <id>` | Inicia a criação de um portal e abre o editor. |
| `/kgate edit <id>` | Abre um portal existente no editor. |
| `/kgate remove <id>` | Remove um portal. |
| `/kgate browse` | Abre a lista de portais existentes. |
| `/kgate go <id> <1/2>` | Teleporta o administrador diretamente ao ponto 1 ou 2. |

### Warps

| Comando | Descrição | Permissão |
| --- | --- | --- |
| `/warp <local>` | Teleporta para um warp. | `kgates.warp` |
| `/warp create <local>` | Cria um warp na posição atual. | `kgates.warp.admin` |
| `/warp remove <local>` | Remove um warp. | `kgates.warp.admin` |

Nomes de warps aceitam de 1 a 32 letras, números, `_` ou `-`.

## Permissões

| Permissão | Padrão | Finalidade |
| --- | --- | --- |
| `kgates.admin` | Operadores | Criar, editar, remover, listar e visitar pontos de portais. |
| `kgates.warp` | Todos | Usar warps existentes. |
| `kgates.warp.admin` | Operadores | Criar e remover warps. |

## Editor de portais

O menu principal reúne estas configurações:

- **Tipo:** uma direção ou duas direções.
- **Pontos A e B:** blocos que representam as duas extremidades.
- **Formato:** esfera, cilindro ou retângulo, com dimensões X, Y e Z aplicáveis.
- **Comandos:** comandos executados pelo jogador na ativação; devem ser informados sem a `/` inicial. Use o prefixo `console:` para executá-los pelo console e `%player%` para inserir o nome do jogador.
- **Condições:** expressões que precisam ser verdadeiras para permitir a passagem.
- **Efeitos:** partículas de ambiente, entrada e saída, quantidades, velocidades, intervalo e sons.
- **Tempo de espera:** cooldown em ticks; 20 ticks correspondem aproximadamente a 1 segundo.

Algumas opções solicitam um valor pelo chat. Digite `cancelar` para abandonar uma entrada pendente e voltar ao editor.

## Condições e placeholders

As condições aceitam placeholders do PlaceholderAPI e os operadores `>=`, `<=`, `!=`, `==`, `>` e `<`. Exemplos:

```text
%player_health% >= 10
%player_level% > 5
%player_world% == world
```

Todas as condições configuradas no portal precisam ser atendidas. Os placeholders disponíveis dependem das expansões instaladas no PlaceholderAPI.

## Armazenamento

O plugin mantém seus dados em:

- `plugins/KGates/gates.yml`: portais e todas as suas configurações.
- `plugins/KGates/warps.yml`: posições dos warps.

As alterações são salvas ao concluir ou remover um portal, ao criar ou remover um warp e quando o plugin é desativado. Faça backup desses arquivos antes de editá-los manualmente. Não há opções gerais de configuração no código atual, embora o plugin prepare um `config.yml` caso esse recurso seja adicionado no futuro.

## Estrutura do projeto

```text
src/main/java/me/erik/kgates/
├── builder/       # Editor visual e entrada de dados pelo chat
├── commands/      # Comandos de warps
├── conditions/    # Condições e integração com PlaceholderAPI
├── listeners/     # Detecção e ativação dos portais
├── manager/       # Modelos, carregamento e persistência
├── Commands.java  # Comandos administrativos dos portais
└── KGates.java    # Inicialização do plugin
```

## Desenvolvimento

O projeto usa Maven e não possui testes automatizados no momento. Para verificar a compilação:

```sh
mvn clean package
```

Como o funcionamento depende dos eventos e das APIs do servidor, recomenda-se também testar manualmente o JAR em uma instância Spigot compatível, com o PlaceholderAPI instalado.
