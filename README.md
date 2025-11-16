# curso-kafka:.

## Anotações gerais:.

````
Consumer = Consome a mensagem
orderDispatcher.send = Posta a mensagem no topico
Podemos armazenar as mensagens com erro ou tentar novamente
Importar os projetos como Maven
Armazenando enquanto processa é um problema, precisamos saber se tem dependência de outro processo
````

## Configuration o Kafka:.

````
docker pull apache/kafka:4.0.0
docker run -p 9092:9092 apache/kafka-native:4.0.0
````

## Configuration o banco sqlite:.

![img.png](img.png) <br>
Criar o banco na pasta target/users_database.db com ajuda do sqlitestudio <br>
![img_1.png](img_1.png) <br>
Apontar corretamente a pasta target do start do projeto<br>
![img_2.png](img_2.png) <br>
Quando roda o createUserService pela primeira vez ele cria a tabela, depois comentar.
![img_3.png](img_3.png) <br>

## Criando um novo serviço:.

````
Representa um banco de usuários - service-users - banco sqlite 3.28.0
Acessar o banco e fazer algo
SQLite - Muito utilizado para pocs (Não precisa rodar no container)

#Criar banco de dados na pasta target/users_database.db
Baixar o sqlite studio -> https://sqlitestudio.pl/ 
````

## Testando:.

````
Rodar:
CreateUserService (Grava os usuarios no banco de dados)
FraudeDetectorService (Topico que processa as nova ordens)
LogService (Loga as mensagens)
EmailService (Envia o email para o usuário)

NewOrderMain (Gera 10 ordens) <- Ultimo (Quando os outros estiverem rodando)
HttpEccomerceService <- Para receber as requests via get no Browser
````

![img_4.png](img_4.png) <br>

## Interação de um serviço web com o Kafka:.

````
Utilizei o jetty para ser simples - https://jetty.org/
doGet - Get que estamos acostumados
localhost:8080/new  <- Chama nossa API
localhost:8080/new?email=gustavocarvalho.ti@gmail.com&amount=153  <- Chama nossa API
````

## Fast delegate:.

````
Quanto menos codigo no HTTP mais facil de replicar
Colocar minimo de processamento possível
````

## Single point of failure do broker:.

````
Cada consumer group consume simultaneo de acordo com o numero de partições
3 partições = 3 consumers
$ docker exec -it dc8cd4d592c6 bash
$ bin/kafka-topics.sh --describe --bootstrao-server localhost:9092 (Descreve os topicos rodando)
O que acontece com os topicos se o Kafka cai? 
O broker é unico, as aplicações reclamam das conecções com a porta 9092
O Broker volta e os apps conseguem reconectar
$ bin/kafka-server-start.sh config/server.properties (Inicia o topico)
Se um service cai o outro assume, como fazer isso para os Brokers?
Como levantar 2 broquers kafka?
$ cp config/server.properties config/server2.properties
$ ci config/server2.properties
Mudar o broker.id=2 (Colocar 2 porque o nome do nosso server é server2.properties)
Mudar o log.dirs=/user.../1552-kafka2/apps/data/kafka (Mudar para 2 tb)
Mudar listeners=PLAINTEXT://:9093 (Fazer o incremento)
$ bin/kafka-server-start.sh config/server2.properties (Inicia o topico 2)

Quando o Leader: 0 cair precisa ir para a replica
$ bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic ECOMMERVE_NEW_ORDER --partitions 3 --replication-factor 2 (Ver a foto abaixo, precisa add antes) 
````

![img_5.png](img_5.png) <br>Adicionar essa propriedade nos 2 brokers <br>

## Apagando os dados do diretorio

````
$ rm -fr ../data/
$ rm -fr ../data/kafka/*
$ rm -fr ../data/zookeeper/*
$ pwd -> apps/kafka_2.12-2.3.1
$ bin/zookeeper-server-start.sh config/zookeeper.properties
$ bin/kafka-server-start.sh config/server.properties
$ bin/kafka-server-start.sh config/servers.properties
$ bin/kafka-topics.sh --describe --bootstrao-server localhost:9092
$ bin/kafka-topics.sh --describe --bootstrao-server localhost:9093
O Leader replica para as replicas
````
