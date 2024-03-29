# Kotlin_Project_XML
## Fase 1 (Modelo de dados)
Foi implementado o padrão de desenho Visitor para a modelação dos dados.
Portanto, existem duas classes - CompositeEntity e SimpleEntity - que se baseiam na classe abstrata Entity.
Exemplo do output do modelo de dados:
```xml
<room>
    <class classname="EB1">
        <person gender="male" age="24">
            <gender>male</gender>
            <age>24</age>
        </person>
        <person gender="female">
            <gender>female</gender>
        </person>
    </class>
</room>
```
A classe CompositeEntity possui uma lista dos seus filhos, portanto, seguindo 
o exemplo acima: room, class e person surgem como classes CompositeEntity, tendo gender e age como classes SimpleEntity porque não vão ter filhos.
Ambas classes (CompositeEntity e SimpleEntity) possuem uma lista de atributos que pode ser definida. Para a execução desta fase, seguir os seguintes passos:
1. Criar um objeto para ser a raiz do XML, portanto a variável parent da classe fica a null. Para passar atributos ao objeto é só criar uma lista mutável do tipo Attribute - nome da classe.
2. Durante a criação do resto dos objetos CompositeEntity/SimpleEntity é necessário passar o objeto que vai ser o pai.

Exemplo da criação do XML mencionado acima, encontra-se no ficheiro Main.kt (caminho: src/main/kotlin/Main.kt)

## Fase 2 (Inferência Automática)
Nesta fase foram definidas 3 anotações:

- XmlName
- XmlIgnore
- XmlTagContent

Estas 3 anotações são utilizadas aquando da criação do data class. Seguir os seguintes passos para a criação do data class:
1. Criar um data class na pasta Data.
2. Definir a estrutura do data class - variaveis
3. Adicionar anotação XmlName para o data class, introduzindo o nome que deve surgir no XML
4. Adicionar anotação XmlIgnore nas variáveis que são para serem ignorada, se for o caso
5. Adicionar anotação XmlTagContent nas variáveis que vão contemplar na geração do XML

Para gerar o XML através do data class criado, seguir os seguintes passos:
1. Criar objeto de acordo com o data class
2. Criar um objeto da classe ModelGenerator (caminho: src/main/kotlin/Classes/ModelGenerator.kt)
3. Gerar o modelo XML pretendido, passando o objeto data class criado para a função createModel do ModelGenerator

Exemplo da criação do XML através da inferência automática, encontra-se no ficheiro Main.kt (caminho: src/main/kotlin/Main.kt)


## Fase 4
Para desenvolver/implementar um widget consoante as suas necessidades será necessário criar uma nova classe no ficheiro AttributesFrames.kt (caminho: src/main/kotlin/Classes/AttributeFrames.kt).
A classe deve implementar a interface AttributeFrameSetup. Os seguintes pontos explicam com mais detalhe a interface:

- Indicar o nome do tipo de atributo na variável typeAttribute
- A função execute serve para notificar o modelo sobre as alterações dependendo do tipo de execução, executar um comando consoante o tipo de ação
- A função getFrame retorna um JPanel. Aqui pode ser construído o widget consoante o gosto de cada um, e poderá executar comandos consoante a ação do utilizador.

A classe Window tem uma variavel que suporta as implementações das classes que implementam a interface AttributeFrameSetup.Exemplos de widgets especializados encontram-se no ficheiro AttributeFrames.kt

No caso, de querer desenvolver uma nova ação no menu de ações será necessário criar uma nova classe no ficheiro Actions.kt (caminho: src/main/kotlin/Classes/Actions.kt).
A classe deve implementar a interface Action. Os seguintes pontos explicam com mais detalhe a interface:

- Indicar o nome do tipo de ação (variável actionName)
- Se a ação só puder ser feita num tipo de entidade, indicar o nome da entidade (variável parentName)
- Indicar o nome da nova entidade que vai ser criada (variável entityName)
- A função execute tem de retornar um objeto JMenuItem. Esse objeto vai ser concatenado à lista de ações.

A classe Window tem uma variavel que suporta a implementação de novas ações para o menu. Exemplo de implementação encontra-se no ficheiro Actions.kt

Para que as novas implementações tenham impacto na Interface Gráfica, é necessário que as novas classes sejam descritas no ficheiro di.properties (caminho: src/main/kotlin/di.properties). Exemplo:

- Caso seja a adição de um novo widget: adicionar a classe na lista de Window.attributesFrames com a seguinte nomenclatura <nome_package>.<nome_classe>
- Caso seja a adição de uma nova ação: adicionar a classe na lista de Window.menuActions com a seguinte nomenclatura <nome_package>.<nome_classe>

