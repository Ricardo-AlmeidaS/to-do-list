# To-Do List

Aplicativo Android de lista de tarefas escrito em Kotlin com Jetpack Compose. O usuário
pode cadastrar, listar, editar, marcar como concluída e excluir tarefas. Tudo fica salvo
no próprio aparelho com o Room, então a lista continua lá depois de fechar o app.

## Funcionalidades

- Cadastrar uma nova tarefa com título e descrição
- Listar todas as tarefas cadastradas
- Editar uma tarefa já existente (basta tocar no card)
- Marcar e desmarcar uma tarefa como concluída
- Excluir uma tarefa
- Navegar entre a tela de lista e a tela de formulário

## Tecnologias

- Kotlin
- Jetpack Compose e Material 3 para a interface
- Room para salvar os dados em um banco SQLite local
- Coroutines e Flow para as operações assíncronas
- ViewModel para guardar o estado da tela
- Navigation Compose para a navegação entre as telas

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
