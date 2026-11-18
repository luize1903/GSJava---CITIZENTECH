
# Territory Predictive Insights
### Global Solution 2026.2 — Programação Orientada a Objetos

**Tema:** CitizenTech — Monitoramento de chuvas e prevenção de alagamentos.

## Integrantes

Lívia RM562967: Domínio e exceções 
Sarah  RM563841 : Persistência 
Luize  RM564016 : Previsões, alertas e testes 

**GitHub:** https://github.com/trolezisarah/GS2026_Java.git

## Sobre o projeto

O Territory Predictive Insights é um sistema desenvolvido em Java para monitorar condições ambientais e auxiliar na prevenção de alagamentos.

O projeto utiliza sensores simulados de chuva, nível do córrego e umidade do solo. As medições são armazenadas em um banco de dados e utilizadas para gerar previsões e alertas.

O sistema foi desenvolvido para a comunidade fictícia Várzea do Córrego, buscando apoiar moradores, lideranças comunitárias e órgãos responsáveis pela prevenção de desastres.

## Funcionalidades

- Simulação e leitura de sensores ambientais.
- Armazenamento de medições e alertas no banco H2.
- Previsões por média móvel e comparação.
- Alertas nos níveis ATENÇÃO, ALERTA e CRÍTICO.
- Consulta ao histórico de medições e alertas.
- Validação de dados e tratamento de exceções.
- Exportação do histórico para CSV.
- Menu interativo no console.

## Tecnologias utilizadas

- **Java 17:** desenvolvimento do sistema.
- **Maven:** gerenciamento de dependências.
- **JDBC:** comunicação com o banco de dados.
- **H2:** armazenamento persistente das informações.
- **JUnit 5:** testes unitários.
- **Mockito:** simulação de dependências nos testes.

## Conceitos de OOP

O projeto aplica abstração, encapsulamento, herança e polimorfismo por meio de classes abstratas, interfaces e implementações de sensores e previsões.

Também utiliza duas exceções personalizadas: `DadosInvalidosException` e `FonteIndisponivelException`.

## Decisões técnicas

Escolhemos JDBC com H2 por ser uma solução simples, leve e adequada para armazenar as medições e os alertas sem precisar de um servidor externo.

Utilizamos os padrões criacionais:

- **Factory:** `FabricaPrevisao`, responsável por criar as estratégias de previsão.
- **Singleton:** `ConfiguracaoSistema`, responsável por centralizar as configurações e os limites de alerta.

Os testes utilizam JUnit 5, o padrão AAA (Arrange, Act, Assert), `assertThrows` e Mockito.

## Reflexão crítica

O sistema precisa de dados suficientes para gerar previsões confiáveis. Com poucas medições ou falhas nos sensores, os resultados podem ser imprecisos, por isso é importante evitar previsões sem informações adequadas.

Além disso, os sensores não representam todas as condições de uma região, pois fatores como drenagem, relevo e infraestrutura também influenciam os alagamentos.

Os alertas servem para apoiar decisões humanas, e não para substituí-las. O objetivo é auxiliar na prevenção e na segurança da comunidade.

## Uso de Inteligência Artificial

Utilizamos IA como ferramenta de apoio para esclarecer dúvidas, compreender conceitos de OOP e auxiliar na organização do código e da documentação.

As sugestões foram revisadas pela equipe de acordo com os requisitos do projeto.

**Observação:** confirmar com as integrantes se esta descrição corresponde ao uso real de IA.

## Divisão de tarefas

**Lívia — RM562967:** desenvolvimento das classes de domínio, sensores, interfaces e exceções personalizadas.

**Sarah — RM563841:** implementação do banco H2, conexão JDBC e classes DAO para armazenamento e consulta dos dados.

**Luize — RM564016:** desenvolvimento das estratégias de previsão, alertas, serviços de monitoramento, menu principal e testes unitários.

As contribuições de cada integrante serão registradas nos commits do GitHub, conforme o trabalho realizado.

## Como executar

É necessário ter Java 17 e Maven instalados.

Abra o terminal na pasta do projeto e execute:

**1. Executar os testes:**

```bash
mvn test
```

**2. Iniciar o sistema:**

```bash
mvn compile exec:java
```

Após iniciar, utilize o menu para realizar leituras, gerar previsões, consultar o histórico e verificar os alertas.

## Conclusão

O projeto permitiu aplicar os conceitos de Programação Orientada a Objetos, persistência de dados, padrões criacionais e testes unitários em uma solução voltada à prevenção de alagamentos.

A plataforma demonstra como a tecnologia pode auxiliar comunidades na identificação de riscos e na tomada de decisões preventivas.
