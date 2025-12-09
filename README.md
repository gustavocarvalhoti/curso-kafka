# Curso-kafka-alura:.

![img_31.png](img_31.png) <br>

## Anotações gerais:.

````
Consumer = Consome a mensagem
orderDispatcher.send = Posta a mensagem no topico
Podemos armazenar as mensagens com erro ou tentar novamente
Importar os projetos como Maven
Armazenando enquanto processa é um problema, precisamos saber se tem dependência de outro processo
kafka = Topico
zookeeper = Gerencia os topicos kafka
````

## Configuration o Kafka:.

````
$ docker pull apache/kafka:4.0.0
$ docker run -p 9092:9092 apache/kafka-native:4.0.0

Adicionei o arquivo docker-compose.yml no projeto, ele já utiliza o zookeeper tb (Sugiro rodar esse)
$ docker compose up

Comandos:
$ docker ps
$ docker exec -it kafka-produto bash
$ cd /opt/kafka/bin
$ kafka-topics.sh --describe --bootstrap-server localhost:9092 (Descreve os topicos rodando)
$ kafka-server-start.sh config/server.properties (Inicia o topico)
$ cp config/server.properties config/server2.properties
$ ci config/server2.properties
$ bin/kafka-server-start.sh config/server2.properties (Inicia o topico 2)
$ pwd -> apps/kafka_2.12-2.3.1
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
$ bin/kafka-server-start.sh config/servers.properties
$ bin/kafka-topics.sh --describe --bootstrap-server localhost:9092
$ bin/kafka-topics.sh --describe --bootstrap-server localhost:9093
$ bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9091 --describe (Lista os grupos rodando)
````

## Configuration o banco sqlite:.

![img.png](img.png) <br>
Criar o banco na pasta target/users_database.db com ajuda do sqlitestudio <br>
![img_1.png](img_1.png) <br>
Apontar corretamente a pasta target do start do projeto <br>
Quando roda o createUserService pela primeira vez ele cria a tabela, depois comentar. <br>
![img_2.png](img_2.png) <br>
![img_3.png](img_3.png) <br>

## Criando um novo serviço:.

````
Representa um banco de usuários - service-users - banco sqlite 3.28.0
Acessar o banco e fazer algo
SQLite - Muito utilizado para pocs (Não precisa rodar no container)

#Criar banco de dados na pasta target/users_database.db
Baixar o sqlite studio -> https://sqlitestudio.pl/
Baixar offset explorer (Ler os topicos) -> https://www.kafkatool.com/download.html
````

## Testando:.

````
Rodar:
CreateUserService (Grava os usuarios no banco de dados)
FraudeDetectorService (Topico que processa as nova ordens)
LogService (Loga as mensagens)
EmailService (Envia o email para o usuário)
ReadingReportService (Gera o relatorio de usuario)
BatchSendMessageService (Gera o relatorio de todos os usuarios)
DeadletterService (Topico de mensagens com erro)

NewOrderMain (Gera 10 ordens) <- Ultimo (Quando os outros estiverem rodando)
HttpEccomerceService <- Para receber as requests via get no Browser
````

![img_29.png](img_29.png) <br>
![img_4.png](img_4.png) <br>
Lembre-se de ajustar o working directory das aplicações. \$MODULE_WORKING_DIR\$ <br>
![img_20.png](img_20.png) <br>
![img_30.png](img_30.png) <br>

## Interação de um serviço web com o Kafka:.

````
Utilizei o jetty para ser simples - https://jetty.org/
doGet - Get que estamos acostumados
localhost:8080/new  <- Chama nossa API
localhost:8080/new?email=gustavocarvalho.ti@gmail.com&amount=153  <- Chama nossa API
localhost:8080/admin/generate-reports
````

## Fast delegate:.

````
Quanto menos codigo no HTTP mais facil de replicar
Colocar minimo de processamento possível
Dar a resposta o mais rápido possível
Criar serviços pequenos e eles ficam observando os topicos
````

## Single point of failure do broker:.

````
Cada consumer group consume simultaneo de acordo com o numero de partições
3 partições = 3 consumers
O que acontece com os topicos se o Kafka cai? 
O broker é unico, as aplicações reclamam das conecções com a porta 9092
O Broker volta e os apps conseguem reconectar
Se um service cai o outro assume, como fazer isso para os Brokers?
Como levantar 2 broquers kafka?
Mudar o broker.id=2 (Colocar 2 porque o nome do nosso server é server2.properties)
Mudar o log.dirs=/user.../1552-kafka2/apps/data/kafka (Mudar para 2 tb)
Mudar listeners=PLAINTEXT://:9093 (Fazer o incremento)

Quando o Leader: 0 cair precisa ir para a replica
$ bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic ECOMMERVE_NEW_ORDER --partitions 3 --replication-factor 2 (Ver a foto abaixo, precisa add antes) 
````

![img_5.png](img_5.png) <br>
Adicionar essa propriedade nos 2 brokers <br>

## Apagando os dados do diretorio

````
$ rm -fr ../data/
$ rm -fr ../data/kafka/*
$ rm -fr ../data/zookeeper/*
````

## Conectando no topico com o offiset explorer

![img_6.png](img_6.png) <br>

## Definição basica dos topicos

![img_9.png](img_9.png) <br>
![img_7.png](img_7.png) <br>
![img_8.png](img_8.png) <br>
4 Brokers <br>
![img_10.png](img_10.png) <br>
Mudar de todos: broker.id, logs.dirs, listeners (Mudar a porta) <br>
Pode apagar os diretorios que ele cria sozinho <br>
![img_11.png](img_11.png) <br>
Logs do Zookeeper <br>
![img_12.png](img_12.png) <br>
Os leader são escolhidos automaticamente <br>
![img_13.png](img_13.png) <br>

## Acks e reliability

````
Imagine que enviamos a mensagem e temos cinco máquinas rodando o cluster. Quatro delas saíram do ar e nós enviamos a
mensagem. A mensagem chega nessa única e não replicou ainda. O que acontece?

Antes da máquina levantar e poder replicar, essa máquina cai e perde os arquivos. Em seguida, outra máquina levanta, ou
seja, ela está com um estado antigo, não com um estado novo, porque não deu tempo de a máquina que caiu enviar as
informações para a réplica.

Estamos com réplica 3, então independentemente de ter cinco máquinas, a réplica é 3. Outra situação é a máquina líder
receber a mensagem, porque o write é sempre para o líder.

Então, mandamos a mensagem para o líder, o líder escreve e informa que está ciente. Em seguida, mandamos replicar. Com
isso, o líder avisa que já escreveu e irá mandar replicar.

Nós assumimos que já foi escrita a mensagem e alguém poderá consumi-la. Porém, nesse meio tempo, o líder cai. Até está
escrito no disco do líder, mas ele não sincronizou com as réplicas. Assim, as réplicas não ficaram sabendo da mensagem
que chegou. O que acontece agora?

Daqui a pouco, essas réplicas percebem que o líder está fora do ar e uma delas vira líder, com as mensagens antigas, sem
a nova mensagem.

Citamos duas situações: uma em que as réplicas caíram e o servidor logo depois; e outra em que o líder caiu e as
réplicas se tornaram líderes sem ter as informações mais recentes, porque quando enviamos a mensagem em
NewOrderServlet.java, na linha 40, chamamos um get() em KafkaDispatcher.java.

0 = Não se preocupa - não retenta <br>
1 = O leader escreve no log local e não espera as replicas (Se as replicas não pegaram a informação se perde) <br>
all = Ele espera todas as réplicas darem OK para confirmar o recebimento da mensagem <br>
````

![img_14.png](img_14.png) <br>
Quando um serviço cai precisa levantar outro rápido. <br>

## Novo projeto Reading Report

````
Pedido de geração de relatório
Relatório de 1 usuário
````

Cria o diretorio e subescreve caso necessario. <br>
![img_16.png](img_16.png) <br>
Escreva no final do arquivo <br>
![img_17.png](img_17.png) <br>
Os relatórios serão gerados na pasta target <br>
![img_18.png](img_18.png) <br>

## A importancia do CorrelationId

````
Trace que a mensagem percorre
Boa pratica que concatena as informações
Add no common-kafka -> CorrelationId
logar o trace das informações -> 
````

## Produzir mensagens no topico

![img_22.png](img_22.png) <br>

## Consumir mensagens do topico

![img_21.png](img_21.png) <br>
Consome 1 mensagem e já avisa o broker <br>
![img_23.png](img_23.png) <br>

## Envio assincrono (Não espera o OK do topico)

![img_25.png](img_25.png) <br>

## Envio sincrono (Espera o OK do topico)

![img_24.png](img_24.png) <br>

## Forçando um erro no envio da mensagem

![img_26.png](img_26.png) <br>
Tentei consumir mas deu erro? Postar para a DEADLETTER, se não conseguir postar pare o serviço. <br>
![img_27.png](img_27.png) <br>
DEADLETTER com erro para tudo. <br>
![img_28.png](img_28.png) <br>

## Kafka producer e consumer

````
Producer = thread safe     = Pode usar ele em várias threads.

Consumer = not thread safe = Não pode usar ele em várias threads.
Indicado 1 consumidor por thread, consumir e despachar para processamento.
Como melhorar isso? 
No nosso EmailService vamos criar outra thread.
````

## Consumer

Antigo <br>
![img_32.png](img_32.png) <br>
Novo <br>
![img_33.png](img_33.png) <br>

## Latest e Earliest

![img_34.png](img_34.png) <br>

## Lidando com mensagens duplicadas

Como receber exatamento 1 vez? <br>
Quando eu recebo o uuid do front eu consigo garantir que a mensagem será processada apenas 1 vez. <br>
![img_35.png](img_35.png) <br>
Validando com orders gravadas no banco de dados <br>
![img_36.png](img_36.png) <br>

## Modulo II - Kafka e Spring: integrando aplicações e gerenciando fluxos de dados

Utilizaremos o kafka templade so spring <br>
![img_37.png](img_37.png) <br>
Se eu tenho 2 consumers e 1 topico com 2 partições ele faz o load balance automatico <br>
![img_38.png](img_38.png) <br>
![img_39.png](img_39.png) <br>
![img_40.png](img_40.png) <br>
Ele distribui a mensagem para as 2 aplicações <br>

## Configurações do consumidor

![img_41.png](img_41.png) <br>
Se tiver 10 mensagens ele traz as 10 e processa (default = 500) <br>
Pode deixar lento a rede com um json muito grande <br>
![img_42.png](img_42.png) <br>
Pega as mensagens a partir do momento que sobe. <br>
![img_43.png](img_43.png) <br>
Pega as mensagens desde a mais antiga do topico. <br>
![img_44.png](img_44.png) <br>
Não cria topicos automaticamente. <br>
![img_45.png](img_45.png) <br>
Desativa o commit automatico, obriga a dar o ack. <br>
![img_46.png](img_46.png) <br>
![img_47.png](img_47.png) <br>