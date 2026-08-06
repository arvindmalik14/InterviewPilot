package com.malik.InterviewPilot.aiqa.config;

import com.malik.InterviewPilot.aiqa.entity.AiCategory;
import com.malik.InterviewPilot.aiqa.entity.AiPlanQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestion;
import com.malik.InterviewPilot.aiqa.entity.AiQuestionCategory;
import com.malik.InterviewPilot.aiqa.repository.AiCategoryRepository;
import com.malik.InterviewPilot.aiqa.repository.AiPlanQuestionRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionCategoryRepository;
import com.malik.InterviewPilot.aiqa.repository.AiQuestionRepository;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Seeds the AI Technical Questions and Answers catalog (categories + curated Q&A content) so the
 * module is usable immediately after first boot. Runs after DataSeeder (@Order(20)) since plan
 * rows must already exist for the plan-question assignment step.
 */
@Component
@RequiredArgsConstructor
@Order(30)
public class AiQuestionSeeder implements CommandLineRunner {

    private final AiCategoryRepository categoryRepository;
    private final AiQuestionRepository questionRepository;
    private final AiQuestionCategoryRepository questionCategoryRepository;
    private final AiPlanQuestionRepository planQuestionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Map<String, AiCategory> categories = seedCategories();
        List<AiQuestion> questions = new ArrayList<>();
        questions.addAll(javaQuestions(categories));
        questions.addAll(springBootQuestions(categories));
        questions.addAll(awsQuestions(categories));
        questions.addAll(azureQuestions(categories));
        questions.addAll(kubernetesQuestions(categories));
        questions.addAll(dockerQuestions(categories));
        questions.addAll(sqlQuestions(categories));
        questions.addAll(microservicesQuestions(categories));
        questions.addAll(systemDesignQuestions(categories));

        seedPlanQuestionAssignments(questions);
    }

    private Map<String, AiCategory> seedCategories() {
        Map<String, String> descriptions = Map.ofEntries(
                Map.entry("Java", "Core language, collections, concurrency, and JVM internals."),
                Map.entry("Spring Boot", "Dependency injection, auto-configuration, and Spring MVC/Security fundamentals."),
                Map.entry("AWS", "Core AWS services, architecture patterns, and the shared responsibility model."),
                Map.entry("Azure", "Core Azure services, governance, and resource management."),
                Map.entry("Kubernetes", "Container orchestration: pods, deployments, services, and cluster internals."),
                Map.entry("Docker", "Images, containers, layered builds, and container networking."),
                Map.entry("SQL", "Relational database querying, indexing, normalization, and transactions."),
                Map.entry("Microservices", "Service decomposition, resilience patterns, and distributed-system tradeoffs."),
                Map.entry("System Design", "Scalability, availability, and the tradeoffs behind large-system architecture."));

        Map<String, AiCategory> saved = new java.util.LinkedHashMap<>();
        for (var entry : descriptions.entrySet()) {
            saved.put(entry.getKey(), categoryRepository.save(
                    AiCategory.builder().name(entry.getKey()).description(entry.getValue()).build()));
        }
        return saved;
    }

    private AiQuestion save(String title, String answer, String example, String difficulty, AiCategory... categories) {
        AiQuestion question = questionRepository.save(AiQuestion.builder()
                .title(title)
                .detailedAnswer(answer)
                .realWorldExample(example)
                .difficultyLevel(difficulty)
                .build());
        for (AiCategory category : categories) {
            questionCategoryRepository.save(AiQuestionCategory.builder().question(question).category(category).build());
        }
        return question;
    }

    private List<AiQuestion> javaQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is dependency injection?",
                        "Dependency injection helps achieve loose coupling between components. In Spring Boot, the "
                                + "framework automatically creates and injects dependencies using annotations such as "
                                + "@Autowired, @Service, and @Component, instead of a class constructing its own "
                                + "collaborators with 'new'.",
                        "A payment service receives a payment gateway object instead of creating it directly, so a "
                                + "test can inject a mock gateway without touching real payment infrastructure.",
                        "EASY", c.get("Java"), c.get("Spring Boot")),
                save("What is the difference between HashMap and ConcurrentHashMap?",
                        "HashMap is not thread-safe — concurrent reads and writes from multiple threads can corrupt "
                                + "its internal bucket structure or produce inconsistent results. ConcurrentHashMap "
                                + "achieves thread safety by partitioning the map into segments and using fine-grained "
                                + "locking (or CAS operations internally), allowing concurrent reads and a high degree "
                                + "of concurrent writes without locking the entire map.",
                        "A shared in-memory cache accessed by multiple request-handling threads in a web server "
                                + "should use ConcurrentHashMap; using a plain HashMap there can cause rare, hard-to-"
                                + "reproduce data corruption under load.",
                        "MEDIUM", c.get("Java")),
                save("What is the difference between == and equals() in Java?",
                        "== compares object references for objects (whether two variables point to the same memory "
                                + "location) and compares primitive values directly for primitives. equals() is a "
                                + "method that can be overridden to compare logical/content equality between two "
                                + "objects — the default Object.equals() implementation just falls back to ==.",
                        "Two separate String objects with the same text are '==' false but 'equals()' true; comparing "
                                + "user-entered emails for a login check must use equals(), not ==.",
                        "EASY", c.get("Java")),
                save("What is the difference between checked and unchecked exceptions?",
                        "Checked exceptions extend Exception (excluding RuntimeException) and must be either caught "
                                + "or declared in a method's throws clause — the compiler enforces handling. Unchecked "
                                + "exceptions extend RuntimeException and represent programming errors or unexpected "
                                + "conditions that the compiler does not force you to handle.",
                        "IOException when reading a file is checked (the file might not exist, so callers must "
                                + "handle it), while NullPointerException from a missing null check is unchecked.",
                        "MEDIUM", c.get("Java")),
                save("How does the JVM garbage collector decide when to reclaim an object?",
                        "The JVM's garbage collector reclaims objects that are no longer reachable from a set of GC "
                                + "roots (active thread stacks, static fields, JNI references). It never uses "
                                + "reference counting for this in modern collectors; instead it periodically traces "
                                + "reachability from those roots and frees everything not reached, often using "
                                + "generational collection to focus effort on short-lived objects.",
                        "A request-scoped object created inside a controller method becomes eligible for collection "
                                + "as soon as the method returns and nothing else still references it.",
                        "HARD", c.get("Java")));
    }

    private List<AiQuestion> springBootQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is the difference between @Component, @Service, and @Repository?",
                        "All three are specializations of Spring's @Component and are detected the same way by "
                                + "component scanning, registering the class as a bean. The distinction is purely "
                                + "semantic/documentation: @Service marks business-logic classes, @Repository marks "
                                + "data-access classes (and additionally enables persistence-exception translation), "
                                + "and @Component is the generic catch-all.",
                        "A PaymentService class is annotated @Service, while the PaymentRepository interface backing "
                                + "it is annotated @Repository, making the architectural layering obvious to readers.",
                        "EASY", c.get("Spring Boot")),
                save("How does Spring Boot auto-configuration work?",
                        "Spring Boot scans the classpath at startup and, using @Conditional annotations (like "
                                + "@ConditionalOnClass or @ConditionalOnMissingBean), registers sensible default beans "
                                + "only when appropriate dependencies are present and the developer hasn't already "
                                + "defined their own bean of that type. This is driven by auto-configuration classes "
                                + "listed in a META-INF/spring/... imports file inside each starter jar.",
                        "Adding spring-boot-starter-data-jpa to the classpath auto-configures a DataSource and "
                                + "EntityManagerFactory automatically, with zero manual bean definitions required.",
                        "MEDIUM", c.get("Spring Boot")),
                save("What is the difference between @RequestParam and @PathVariable?",
                        "@RequestParam extracts a value from the query string or form data (e.g. ?page=2), while "
                                + "@PathVariable extracts a value from a placeholder in the URI template itself (e.g. "
                                + "/users/{id}). Both can be made optional/required and support type conversion.",
                        "GET /users/42?includeOrders=true uses @PathVariable for 42 (the user id) and @RequestParam "
                                + "for includeOrders.",
                        "EASY", c.get("Spring Boot")),
                save("Explain how Spring manages bean lifecycle and scopes.",
                        "By default, Spring beans are singleton-scoped: one instance per application context, "
                                + "created eagerly at startup unless marked lazy. Other scopes include prototype (a "
                                + "new instance per injection point) and web-specific scopes like request/session. "
                                + "Lifecycle callbacks (@PostConstruct, @PreDestroy, or InitializingBean/DisposableBean) "
                                + "let a bean run initialization or cleanup logic as the container creates or destroys it.",
                        "A shared JdbcTemplate bean is singleton-scoped (safe to reuse across requests), while a "
                                + "stateful per-request builder object might be declared prototype-scoped.",
                        "HARD", c.get("Spring Boot")),
                save("What does @Transactional do when applied to a service method?",
                        "@Transactional wraps the method call in a database transaction managed by Spring's "
                                + "transaction infrastructure (via an AOP proxy). If the method completes normally the "
                                + "transaction commits; if it throws an unchecked exception, the transaction rolls "
                                + "back automatically. Checked exceptions don't trigger rollback unless configured to.",
                        "A 'transferFunds' method that debits one account and credits another is annotated "
                                + "@Transactional so that, if the credit step fails, the earlier debit is rolled back too.",
                        "MEDIUM", c.get("Spring Boot")));
    }

    private List<AiQuestion> awsQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is the difference between Amazon SQS and Amazon SNS?",
                        "SQS (Simple Queue Service) is a pull-based message queue: one or more consumers poll the "
                                + "queue and each message is typically processed by exactly one consumer. SNS "
                                + "(Simple Notification Service) is a push-based publish/subscribe service: a single "
                                + "published message can fan out to many subscribers (SQS queues, Lambda, email, "
                                + "HTTP endpoints) simultaneously.",
                        "An order-placed event is published to an SNS topic, which fans out to an SQS queue for "
                                + "inventory processing and a separate SQS queue for sending confirmation emails.",
                        "MEDIUM", c.get("AWS")),
                save("What is the AWS Shared Responsibility Model?",
                        "It splits security duties between AWS and the customer: AWS is responsible for the "
                                + "security 'of' the cloud — physical data centers, host infrastructure, and the "
                                + "virtualization layer. The customer is responsible for security 'in' the cloud — "
                                + "data encryption, IAM policies, network configuration, and OS/application patching "
                                + "on anything they control.",
                        "AWS patches the physical hypervisor, but a customer running EC2 instances is still "
                                + "responsible for patching the guest OS and configuring security groups correctly.",
                        "EASY", c.get("AWS")),
                save("Explain the difference between S3 and EBS.",
                        "S3 is object storage accessed over HTTP(S), designed for durability and scale, and is not "
                                + "attached to a single instance — many instances or services can read the same "
                                + "objects concurrently. EBS is block storage attached to a single EC2 instance at a "
                                + "time (like a virtual hard disk), used for OS volumes and databases that need "
                                + "low-latency block-level access.",
                        "Application logs and user-uploaded files are stored in S3, while the database data files for "
                                + "an EC2-hosted MySQL instance live on an attached EBS volume.",
                        "MEDIUM", c.get("AWS")),
                save("What is an Auto Scaling Group and why is it used?",
                        "An Auto Scaling Group (ASG) automatically launches or terminates EC2 instances to keep the "
                                + "fleet size within a configured min/max range, scaling in response to metrics like "
                                + "CPU utilization or a schedule. It also replaces unhealthy instances automatically, "
                                + "improving both cost efficiency and availability.",
                        "An e-commerce site configures an ASG to scale from 2 to 20 instances automatically during a "
                                + "flash sale, then scale back down afterward to control cost.",
                        "MEDIUM", c.get("AWS")),
                save("How does AWS Lambda achieve serverless execution?",
                        "Lambda runs your code in ephemeral, managed execution environments that AWS provisions on "
                                + "demand in response to a trigger (an API call, an S3 event, a queue message). You "
                                + "are billed only for actual execution time and never manage or provision the "
                                + "underlying servers; AWS handles scaling, patching, and availability.",
                        "An image-upload event on S3 triggers a Lambda function that generates a thumbnail, without "
                                + "any server having to run continuously waiting for uploads.",
                        "HARD", c.get("AWS")));
    }

    private List<AiQuestion> azureQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is Azure Resource Manager (ARM) and why is it important?",
                        "ARM is the deployment and management layer for Azure: every operation (via portal, CLI, or "
                                + "SDK) goes through ARM, which lets resources be organized into resource groups, "
                                + "tagged, access-controlled with a single RBAC model, and deployed declaratively via "
                                + "ARM/Bicep templates for repeatable infrastructure-as-code.",
                        "A team defines an ARM template describing a web app, its database, and its storage account "
                                + "together, so every environment (dev/staging/prod) is provisioned identically.",
                        "MEDIUM", c.get("Azure")),
                save("What is the difference between Azure Blob Storage and Azure Files?",
                        "Blob Storage stores unstructured object data (images, backups, logs) accessed over "
                                + "HTTP(S) via REST APIs or SDKs. Azure Files provides fully managed file shares "
                                + "accessible via the standard SMB or NFS protocols, so it can be mounted like a "
                                + "traditional network drive by both cloud and on-premises machines.",
                        "A legacy Windows application that expects a mapped network drive uses Azure Files, while a "
                                + "web app storing user-uploaded photos uses Blob Storage.",
                        "MEDIUM", c.get("Azure")),
                save("Explain Azure Availability Zones vs Availability Sets.",
                        "Availability Sets protect against hardware failure within a single data center by spreading "
                                + "VMs across different physical racks (fault domains) and host update groups (update "
                                + "domains). Availability Zones go further, spreading resources across physically "
                                + "separate data centers within a region, protecting against a full data-center-level "
                                + "outage, not just a rack failure.",
                        "A mission-critical service is deployed across three Availability Zones in a region so it "
                                + "survives an entire data center going offline.",
                        "HARD", c.get("Azure")),
                save("What is Azure Active Directory (Microsoft Entra ID) used for?",
                        "It is Azure's cloud-based identity and access management service: it authenticates users, "
                                + "issues tokens for single sign-on across applications, manages role-based access "
                                + "control, and supports multi-factor authentication and conditional access policies.",
                        "Employees sign in once to Microsoft Entra ID and get single sign-on access to Microsoft 365, "
                                + "an internal HR portal, and third-party SaaS apps without re-entering credentials.",
                        "EASY", c.get("Azure")),
                save("How does Azure Functions differ from Azure App Service?",
                        "Azure Functions is event-driven, serverless compute: code runs only in response to a "
                                + "trigger and you're billed per execution (or on a fixed plan if chosen), with "
                                + "automatic scaling to zero when idle. Azure App Service hosts long-running web "
                                + "applications and APIs on an always-on (or scaled) set of instances that you "
                                + "provision and pay for continuously.",
                        "A scheduled nightly report-generation job is a good fit for Azure Functions, while a "
                                + "customer-facing web application with constant traffic runs on App Service.",
                        "MEDIUM", c.get("Azure")));
    }

    private List<AiQuestion> kubernetesQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is a Kubernetes Pod?",
                        "A Pod is the smallest deployable unit in Kubernetes: one or more tightly coupled containers "
                                + "that share the same network namespace (IP address and port space) and can share "
                                + "storage volumes. Containers within a Pod are always scheduled together on the "
                                + "same node.",
                        "A main application container and a logging 'sidecar' container that ships its logs are "
                                + "deployed together in the same Pod so they can communicate over localhost.",
                        "EASY", c.get("Kubernetes")),
                save("Explain the difference between a Deployment and a StatefulSet.",
                        "A Deployment manages a set of interchangeable, stateless Pod replicas — any replica can be "
                                + "replaced by an identical new one with a new random name/IP. A StatefulSet manages "
                                + "Pods that need a stable identity (a predictable, persistent name like pod-0, "
                                + "pod-1) and stable per-replica storage across restarts, which stateful workloads "
                                + "like databases require.",
                        "A stateless REST API runs as a Deployment, while a 3-node Kafka or Elasticsearch cluster "
                                + "runs as a StatefulSet so each node keeps its own persistent volume and identity.",
                        "MEDIUM", c.get("Kubernetes")),
                save("What is a Kubernetes Service and why is it needed?",
                        "Pods are ephemeral and get new IP addresses whenever they're recreated, so a Service "
                                + "provides a stable virtual IP and DNS name that load-balances traffic across the "
                                + "current set of matching Pods (selected by labels), decoupling clients from any "
                                + "individual Pod's lifecycle.",
                        "A frontend Pod calls 'http://backend-service' rather than a specific backend Pod's IP, so "
                                + "backend Pods can be replaced or scaled without breaking the frontend.",
                        "EASY", c.get("Kubernetes")),
                save("How does Kubernetes handle self-healing?",
                        "The kubelet on each node continuously checks container health via configured liveness and "
                                + "readiness probes; a failing container is restarted automatically. At the cluster "
                                + "level, controllers (like the Deployment controller) constantly reconcile actual "
                                + "state against desired state, recreating Pods on healthy nodes if a node fails "
                                + "entirely.",
                        "If a worker node crashes, the Pods it was running are rescheduled onto other healthy nodes "
                                + "automatically, without any manual intervention.",
                        "MEDIUM", c.get("Kubernetes")),
                save("What is the role of etcd in a Kubernetes cluster?",
                        "etcd is a distributed, consistent key-value store that serves as the single source of "
                                + "truth for all cluster state — every object (Pods, Services, ConfigMaps, and their "
                                + "desired state) is persisted there. The API server is the only component that "
                                + "talks to etcd directly; every other component reads/writes cluster state through it.",
                        "When you run 'kubectl apply', the API server validates and writes the new desired state "
                                + "into etcd, and controllers watching etcd notice the change and act on it.",
                        "HARD", c.get("Kubernetes")));
    }

    private List<AiQuestion> dockerQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is the difference between a Docker image and a Docker container?",
                        "An image is a read-only, immutable template — a packaged filesystem plus metadata describing "
                                + "how to run an application. A container is a running (or stopped) instance created "
                                + "from an image, with its own writable layer on top; you can start many independent "
                                + "containers from the same single image.",
                        "The same 'myapp:1.0' image can be used to start ten separate containers for load testing, "
                                + "each with independent runtime state.",
                        "EASY", c.get("Docker")),
                save("Explain Docker layers and why they matter for build performance.",
                        "Each instruction in a Dockerfile (FROM, RUN, COPY, etc.) creates a new, cached filesystem "
                                + "layer. Docker reuses cached layers for any instruction whose inputs haven't "
                                + "changed, so ordering instructions from least-to-most frequently changing (e.g. "
                                + "installing dependencies before copying application source) dramatically speeds up "
                                + "rebuilds.",
                        "Putting 'COPY package.json' and 'RUN npm install' before 'COPY . .' means dependency "
                                + "installation is skipped on rebuilds unless package.json itself changed.",
                        "MEDIUM", c.get("Docker")),
                save("What is the purpose of a Dockerfile's multi-stage build?",
                        "Multi-stage builds let you use one stage (with a full SDK/build toolchain) to compile or "
                                + "build an application, then copy only the resulting artifact into a much smaller "
                                + "final stage (e.g. a minimal JRE or alpine image), keeping the shipped image small "
                                + "and free of build-time tooling and source code.",
                        "A Java app is compiled in a 'maven' build stage, then only the resulting .jar is copied into "
                                + "a slim 'eclipse-temurin-jre' final image, cutting the shipped image size drastically.",
                        "MEDIUM", c.get("Docker")),
                save("How does Docker networking work by default?",
                        "By default, Docker creates a 'bridge' network on the host; each container gets its own "
                                + "network namespace and a private IP on that bridge, and can reach other containers "
                                + "on the same bridge network by name via Docker's embedded DNS. Ports must be "
                                + "explicitly published (-p) to be reachable from outside the host.",
                        "Two containers on the same custom bridge network can reach each other as 'http://db:5432' "
                                + "using the service name as a hostname, without any manual IP configuration.",
                        "HARD", c.get("Docker")),
                save("What is the difference between CMD and ENTRYPOINT in a Dockerfile?",
                        "ENTRYPOINT defines the fixed executable that always runs when the container starts, while "
                                + "CMD supplies default arguments that can be overridden at 'docker run' time. When "
                                + "both are set, CMD's value is passed as arguments to ENTRYPOINT.",
                        "ENTRYPOINT [\"java\", \"-jar\", \"app.jar\"] with CMD [\"--spring.profiles.active=dev\"] lets "
                                + "a user override just the profile at runtime without changing the fixed java command.",
                        "MEDIUM", c.get("Docker")));
    }

    private List<AiQuestion> sqlQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is the difference between INNER JOIN and LEFT JOIN?",
                        "INNER JOIN returns only rows that have a matching row in both tables. LEFT JOIN returns "
                                + "every row from the left table regardless of a match, filling in NULLs for columns "
                                + "from the right table when no match exists.",
                        "Listing 'all customers and their orders, including customers with zero orders' requires a "
                                + "LEFT JOIN from customers to orders; an INNER JOIN would silently drop those customers.",
                        "EASY", c.get("SQL")),
                save("Explain database normalization and why it matters.",
                        "Normalization is the process of organizing tables to reduce data redundancy and avoid "
                                + "update anomalies, typically by splitting data into smaller related tables (e.g. "
                                + "moving repeating customer-address data into its own table) and enforcing "
                                + "relationships via foreign keys, following forms like 1NF, 2NF, and 3NF.",
                        "Storing a customer's address once in a 'customers' table instead of repeating it on every "
                                + "row of an 'orders' table prevents inconsistent addresses if the customer moves.",
                        "MEDIUM", c.get("SQL")),
                save("What is the difference between a clustered and non-clustered index?",
                        "A clustered index determines the physical storage order of the table's rows — there can be "
                                + "only one per table, since rows can only be sorted one way on disk. A "
                                + "non-clustered index is a separate structure that stores pointers back to the "
                                + "actual rows, and a table can have many of them.",
                        "The primary key often backs the clustered index (rows physically ordered by id), while a "
                                + "non-clustered index on 'email' speeds up login lookups without reordering the table.",
                        "HARD", c.get("SQL")),
                save("What is a database transaction and what does ACID mean?",
                        "A transaction groups multiple operations so they succeed or fail as a single unit. ACID "
                                + "describes the guarantees: Atomicity (all-or-nothing), Consistency (the database "
                                + "moves between valid states), Isolation (concurrent transactions don't see each "
                                + "other's uncommitted changes), and Durability (committed changes survive a crash).",
                        "Transferring money between two bank accounts debits one and credits the other inside a "
                                + "single transaction, so a crash midway never leaves money debited but not credited.",
                        "MEDIUM", c.get("SQL")),
                save("Explain the N+1 query problem and how to avoid it.",
                        "The N+1 problem happens when code fetches a list of N parent records, then issues one "
                                + "additional query per parent to fetch related child data — resulting in N+1 total "
                                + "queries instead of a small, fixed number. It's commonly fixed with eager/batch "
                                + "fetching (e.g. a JOIN FETCH in JPQL) or explicit batch loading.",
                        "Fetching 100 orders and then querying each order's line items individually in a loop issues "
                                + "101 queries; a single JOIN query (or a batched IN query) replaces all of them.",
                        "HARD", c.get("SQL")));
    }

    private List<AiQuestion> microservicesQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("What is the difference between orchestration and choreography in microservices?",
                        "Orchestration uses a central coordinator that explicitly directs each service on what to do "
                                + "and in what order (a single point of control and visibility). Choreography has no "
                                + "central coordinator — each service reacts to events it observes and publishes its "
                                + "own events, so the overall flow emerges from independent, decentralized reactions.",
                        "A central 'OrderSaga' service calling each step (reserve inventory, charge payment, ship) "
                                + "is orchestration; each service independently reacting to an 'OrderPlaced' event on "
                                + "a message bus is choreography.",
                        "HARD", c.get("Microservices")),
                save("How do microservices typically handle distributed transactions?",
                        "Classic two-phase-commit transactions across services are avoided because they don't scale "
                                + "and create tight coupling. Instead, microservices commonly use the Saga pattern: a "
                                + "sequence of local transactions, each publishing an event that triggers the next "
                                + "step, with explicit compensating actions to undo prior steps if a later step fails.",
                        "If payment fails after inventory was already reserved in an order Saga, a compensating "
                                + "'ReleaseInventory' step runs to undo the reservation.",
                        "HARD", c.get("Microservices")),
                save("What is the API Gateway pattern?",
                        "An API Gateway is a single entry point that sits in front of many microservices, handling "
                                + "cross-cutting concerns like authentication, rate limiting, request routing, and "
                                + "response aggregation, so individual services don't each need to reimplement them "
                                + "and clients don't need to know every service's address.",
                        "A mobile app calls one gateway endpoint that internally fans out to the user, order, and "
                                + "inventory services and combines their responses into one payload.",
                        "MEDIUM", c.get("Microservices")),
                save("Explain the Circuit Breaker pattern and why it's needed.",
                        "A circuit breaker wraps calls to a remote service and tracks failures; once failures exceed "
                                + "a threshold, it 'opens' and short-circuits further calls immediately (failing fast "
                                + "or falling back) instead of letting requests pile up waiting on a struggling "
                                + "dependency. After a cooldown it allows a few trial requests through to see if the "
                                + "dependency has recovered.",
                        "If the recommendation service is timing out, a circuit breaker stops calling it and instead "
                                + "returns a default 'no recommendations' response, preventing the slowdown from "
                                + "cascading to the whole page.",
                        "MEDIUM", c.get("Microservices")),
                save("What is service discovery and why do microservices need it?",
                        "In a dynamic environment where service instances scale up/down and get new IPs constantly, "
                                + "service discovery lets services find each other's current network location at "
                                + "runtime via a registry, rather than relying on hardcoded addresses that would "
                                + "quickly become stale.",
                        "A newly started 'inventory-service' instance registers itself with the discovery registry, "
                                + "and the 'order-service' looks up a healthy instance's address from that registry "
                                + "instead of a hardcoded IP.",
                        "MEDIUM", c.get("Microservices")));
    }

    private List<AiQuestion> systemDesignQuestions(Map<String, AiCategory> c) {
        return List.of(
                save("Explain the CAP theorem.",
                        "The CAP theorem states that a distributed data store can only guarantee two of three "
                                + "properties at the same time during a network partition: Consistency (every read "
                                + "sees the latest write), Availability (every request gets a non-error response), "
                                + "and Partition tolerance (the system keeps working despite network splits). Since "
                                + "partitions are unavoidable in practice, real systems choose to favor either "
                                + "consistency (CP) or availability (AP) when a partition occurs.",
                        "A globally distributed shopping cart service might favor availability (AP) — always accept "
                                + "cart updates even if some replicas are briefly out of sync — over strict consistency.",
                        "HARD", c.get("System Design"), c.get("Microservices")),
                save("What is the difference between horizontal and vertical scaling?",
                        "Vertical scaling ('scaling up') means adding more resources (CPU, RAM) to a single existing "
                                + "machine, which has a hard ceiling and a single point of failure. Horizontal "
                                + "scaling ('scaling out') means adding more machines that share the load, which "
                                + "scales further and improves fault tolerance but requires the application to "
                                + "support running multiple instances (e.g. being stateless).",
                        "Upgrading a database server to a bigger instance type is vertical scaling; adding more web "
                                + "server instances behind a load balancer is horizontal scaling.",
                        "EASY", c.get("System Design")),
                save("What is a load balancer and what algorithms does it use?",
                        "A load balancer distributes incoming traffic across multiple backend servers to avoid "
                                + "overloading any single one and to improve availability. Common algorithms include "
                                + "round robin (rotate evenly), least connections (send to the least-busy server), "
                                + "and weighted variants that account for servers with different capacities.",
                        "A load balancer in front of five identical API servers uses round robin so each server "
                                + "receives roughly a fifth of the traffic.",
                        "MEDIUM", c.get("System Design")),
                save("How does a Content Delivery Network (CDN) improve performance?",
                        "A CDN caches static (and sometimes dynamic) content on servers ('edge locations') "
                                + "geographically close to end users, so requests are served from a nearby edge "
                                + "instead of traveling all the way to the origin server, reducing latency and "
                                + "offloading traffic from the origin.",
                        "A user in Mumbai downloading a video hosted on a US origin server instead gets it from a "
                                + "CDN edge node in Mumbai, cutting load time dramatically.",
                        "EASY", c.get("System Design")),
                save("What is database sharding and when should it be used?",
                        "Sharding splits a single large dataset across multiple independent database instances "
                                + "('shards'), each holding a subset of the data (commonly partitioned by a shard "
                                + "key like user id range or hash). It's used when a dataset or write throughput "
                                + "exceeds what a single database server can handle, at the cost of added complexity "
                                + "for cross-shard queries and transactions.",
                        "A social network shards its users table by user-id hash across 16 database instances so no "
                                + "single database has to hold billions of rows alone.",
                        "HARD", c.get("System Design")));
    }

    private void seedPlanQuestionAssignments(List<AiQuestion> allQuestions) {
        if (planQuestionRepository.count() > 0) {
            return;
        }
        List<SubscriptionPlan> tiersLowToHigh = List.of(
                findPlanOrThrow("Free"), findPlanOrThrow("Basic"),
                findPlanOrThrow("Premium"), findPlanOrThrow("Enterprise"));

        List<AiPlanQuestion> assignments = new ArrayList<>();
        for (int i = 0; i < allQuestions.size(); i++) {
            AiQuestion question = allQuestions.get(i);
            int minTier = i % tiersLowToHigh.size();
            for (int tier = minTier; tier < tiersLowToHigh.size(); tier++) {
                assignments.add(AiPlanQuestion.builder().plan(tiersLowToHigh.get(tier)).question(question).build());
            }
        }
        planQuestionRepository.saveAll(assignments);
    }

    private SubscriptionPlan findPlanOrThrow(String planName) {
        return subscriptionPlanRepository.findByPlanName(planName)
                .orElseThrow(() -> new IllegalStateException("Expected seeded plan not found: " + planName));
    }
}
