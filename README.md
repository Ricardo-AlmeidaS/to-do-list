# To-Do List

Aplicativo Android de lista de tarefas escrito em Kotlin com Jetpack Compose. O usuário
pode cadastrar, listar, editar, marcar como concluída e excluir tarefas. Tudo fica salvo
no próprio aparelho com o Room, então a lista continua lá depois de fechar o app.

## Funcionalidades

- Cadastrar uma nova tarefa com título e descrição
- Listar todas as tarefas cadastradas
- Editar uma tarefa já existente (basta tocar no card)
- Marcar e desmarcar uma tarefa como concluída
- Excluir uma tarefa, com um diálogo pedindo confirmação antes
- Navegar entre a tela de lista e a tela de formulário

## Tecnologias

- Kotlin
- Jetpack Compose e Material 3 para a interface
- Room para salvar os dados em um banco SQLite local
- Coroutines e Flow para as operações assíncronas
- ViewModel para guardar o estado da tela
- Navigation Compose para a navegação entre as telas

## Arquitetura

O projeto é dividido em camadas para que cada parte tenha uma responsabilidade só. A tela
nunca fala direto com o banco: ela pede tudo para o ViewModel, que por sua vez usa o
repositório.

```
MainActivity
     |
AppNavigation (NavHost)
     |
     +-- ListaTarefasScreen
     +-- FormularioTarefaScreen
     |
TarefaViewModel
     |
TarefaRepository
     |
TarefaDao -> TarefaDatabase (Room)
```

## Estrutura de pastas

```
app/src/main/java/com/github/ricardoalmeidas/to_do_list/
├── MainActivity.kt
├── data/          Tarefa (Entity), TarefaDao e TarefaDatabase
├── navigation/    AppNavigation com as rotas do app
├── repository/    TarefaRepository
├── ui/screens/    ListaTarefasScreen e FormularioTarefaScreen
├── ui/theme/      cores, tipografia e tema do Compose
└── viewmodel/     TarefaViewModel
```

## Como cada parte funciona

### TarefaRepository

É a camada que conversa com o DAO e esconde o Room do resto do app. Ele expõe
`tarefas: Flow<List<Tarefa>>`, que vem direto do `TarefaDao.listarTodas()`, e as funções
`inserir`, `atualizar` e `deletar`, todas `suspend`.

A vantagem é que, se um dia os dados viessem de uma API em vez do banco local, só o
repositório precisaria mudar. O ViewModel e as telas continuariam iguais.

### TarefaViewModel

Guarda o estado da tela seguindo o padrão MVVM.

- Transforma o `Flow` do repositório em `StateFlow` com `stateIn`, usando
  `SharingStarted.WhileSubscribed(5_000)`. Assim o fluxo fica ativo por mais 5 segundos
  depois que a tela para de observar, e uma rotação de tela não força tudo a recomeçar.
- Guarda em `tarefaParaExcluir: StateFlow<Tarefa?>` a tarefa que está esperando
  confirmação de exclusão. `solicitarExclusao(tarefa)` abre o diálogo,
  `cancelarExclusao()` fecha sem mexer na lista e `confirmarExclusao()` fecha e chama
  `deletar` só com a tarefa guardada. Por ficar no ViewModel, o diálogo sobrevive a uma
  rotação de tela.
- As ações `inserir`, `atualizar` e `deletar` rodam dentro do `viewModelScope`, então são
  canceladas sozinhas se o ViewModel for destruído.
- O `companion object` tem uma `factory` que monta o `TarefaRepository` a partir do
  `TarefaDao`. O projeto não usa Hilt nem nenhuma outra biblioteca de injeção de
  dependência, a criação é feita na mão mesmo.

### ListaTarefasScreen

- Lê o estado com `collectAsStateWithLifecycle()`, então a tela se redesenha sozinha toda
  vez que o `StateFlow` emite uma lista nova.
- Mostra as tarefas em uma `LazyColumn`, cada uma dentro de um `Card` com checkbox,
  título, descrição e botão de excluir.
- Nenhuma ação altera o estado direto na tela. O checkbox chama `viewModel.atualizar(...)`,
  a lixeira chama `viewModel.solicitarExclusao(...)`, e o clique no card ou no botão "+" só navega.
- Quando existe uma tarefa para excluir, a tela mostra um `AlertDialog` do Material 3 por
  cima da lista (sem abrir outra tela) com o título da tarefa e os botões **Cancelar** e
  **Excluir**. As evidências do fluxo estão em [EVIDENCIAS_EXCLUSAO.md](EVIDENCIAS_EXCLUSAO.md).
- A interface em si fica em `ListaTarefasContent`, que recebe apenas dados e callbacks.
  Isso deixa o `@Preview` funcionar sem precisar de um ViewModel.

### FormularioTarefaScreen

A mesma tela serve para criar e para editar, quem decide é o id que chega pela rota:

| Condição | O que acontece |
|---|---|
| `tarefaId == 0` | Tarefa nova, formulário abre em branco |
| `tarefaId != 0` | Edição, os campos já vêm preenchidos com a tarefa encontrada |

Na hora de salvar o mesmo id decide entre `viewModel.inserir(...)` e
`viewModel.atualizar(...)`, mantendo o id original na edição. Depois de salvar a navegação
volta para a lista, e o título da `TopAppBar` muda entre "Nova Tarefa" e "Editar Tarefa".

### AppNavigation

| Rota | Tela |
|---|---|
| `lista` | `ListaTarefasScreen` (tela inicial) |
| `formulario/{tarefaId}` | `FormularioTarefaScreen` |

Para criar uma tarefa a navegação vai para `formulario/0`, e para editar vai para
`formulario/$id` com o id real. O argumento é lido com
`backStackEntry.arguments?.getString("tarefaId")?.toInt() ?: 0`. O botão de voltar usa
`popBackStack()`, sem empilhar uma tela nova.

### MainActivity

No `onCreate` ela chama `setContent { }` envolvendo tudo no `TodolistTheme`, cria o
`TarefaViewModel` pela factory (que monta banco, DAO e repositório) e passa essa mesma
instância para o `AppNavigation`. Como as duas telas recebem o mesmo ViewModel, o
formulário consegue encontrar a tarefa que está sendo editada.

## Testes

Os testes instrumentados do DAO ficam em
`app/src/androidTest/.../data/TarefaDaoTest.kt` e usam um banco Room em memória para
verificar inserção, atualização e exclusão. Para rodar:

```
./gradlew connectedAndroidTest
```

## Como executar

1. Baixe ou clone este repositório.
2. Abra o Android Studio, escolha **Open** e selecione a pasta raiz do projeto (onde ficam
   os arquivos `build.gradle.kts` e `settings.gradle.kts`).
3. Espere o Gradle Sync terminar. Na primeira vez ele baixa todas as dependências.
4. Suba um emulador ou conecte um celular com a depuração USB ligada.
5. Clique em **Run** (ou `Shift + F10`) para instalar e abrir o app.

## Requisitos

- Android Studio atualizado
- JDK 21
- minSdk 24 e targetSdk 36
