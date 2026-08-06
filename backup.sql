-- InterviewPilot MySQL database backup
-- Database: interviewpilot
-- Generated: 2026-08-06 21:12:57
-- Contains: full schema (DROP + CREATE TABLE) and full data (INSERT) for every table.
-- Restore: mysql -u <user> -p interviewpilot < backup.sql

SET FOREIGN_KEY_CHECKS=0;
SET UNIQUE_CHECKS=0;
SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
USE `interviewpilot`;

-- ============================================================================
-- ai_categories
-- ============================================================================
DROP TABLE IF EXISTS `ai_categories`;
CREATE TABLE `ai_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKqjvtvksndmfw6vmwx77yqqe67` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 8 row(s)
INSERT INTO `ai_categories` (`id`, `created_at`, `description`, `name`, `status`, `updated_at`) VALUES
(1, '2026-08-06 12:14:48.037185', 'Core AWS services, architecture patterns, and the shared responsibility model.', 'AWS', 'ACTIVE', '2026-08-06 12:14:48.037185'),
(2, '2026-08-06 12:14:48.044166', 'Core language, collections, concurrency, and JVM internals.', 'Java', 'ACTIVE', '2026-08-06 12:14:48.044166'),
(4, '2026-08-06 12:14:48.057167', 'Service decomposition, resilience patterns, and distributed-system tradeoffs.', 'Microservices', 'ACTIVE', '2026-08-06 12:14:48.057167'),
(5, '2026-08-06 12:14:48.064246', 'Container orchestration: pods, deployments, services, and cluster internals.', 'Kubernetes', 'ACTIVE', '2026-08-06 12:14:48.064246'),
(6, '2026-08-06 12:14:48.069170', 'Core Azure services, governance, and resource management.', 'Azure', 'ACTIVE', '2026-08-06 12:14:48.069170'),
(7, '2026-08-06 12:14:48.074167', 'Relational database querying, indexing, normalization, and transactions.', 'SQL', 'ACTIVE', '2026-08-06 12:14:48.074167'),
(8, '2026-08-06 12:14:48.080167', 'Dependency injection, auto-configuration, and Spring MVC/Security fundamentals.', 'Spring Boot', 'ACTIVE', '2026-08-06 12:14:48.080167'),
(9, '2026-08-06 12:14:48.087167', 'Images, containers, layered builds, and container networking.', 'Docker', 'ACTIVE', '2026-08-06 12:14:48.087167');

-- ============================================================================
-- ai_plan_question
-- ============================================================================
DROP TABLE IF EXISTS `ai_plan_question`;
CREATE TABLE `ai_plan_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `plan_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_plan_question` (`plan_id`,`question_id`),
  KEY `idx_ai_plan_question_plan_id` (`plan_id`),
  KEY `idx_ai_plan_question_question_id` (`question_id`),
  CONSTRAINT `FK830b44dtjwqvig34rg8wyn6uy` FOREIGN KEY (`question_id`) REFERENCES `ai_questions` (`id`),
  CONSTRAINT `FK9wyr2qhxbpf4btgcod4piw79d` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 111 row(s)
INSERT INTO `ai_plan_question` (`id`, `created_at`, `plan_id`, `question_id`) VALUES
(1, '2026-08-06 12:14:48.594072', 1, 1),
(2, '2026-08-06 12:14:48.594072', 2, 1),
(3, '2026-08-06 12:14:48.594072', 3, 1),
(4, '2026-08-06 12:14:48.594072', 4, 1),
(5, '2026-08-06 12:14:48.594072', 2, 2),
(6, '2026-08-06 12:14:48.594072', 3, 2),
(7, '2026-08-06 12:14:48.594072', 4, 2),
(8, '2026-08-06 12:14:48.594072', 3, 3),
(9, '2026-08-06 12:14:48.594072', 4, 3),
(10, '2026-08-06 12:14:48.594072', 4, 4),
(11, '2026-08-06 12:14:48.594072', 1, 5),
(12, '2026-08-06 12:14:48.594072', 2, 5),
(13, '2026-08-06 12:14:48.594072', 3, 5),
(14, '2026-08-06 12:14:48.594072', 4, 5),
(15, '2026-08-06 12:14:48.594072', 2, 6),
(16, '2026-08-06 12:14:48.594072', 3, 6),
(17, '2026-08-06 12:14:48.594072', 4, 6),
(18, '2026-08-06 12:14:48.594072', 3, 7),
(19, '2026-08-06 12:14:48.594072', 4, 7),
(20, '2026-08-06 12:14:48.594072', 4, 8),
(21, '2026-08-06 12:14:48.594072', 1, 9),
(22, '2026-08-06 12:14:48.594072', 2, 9),
(23, '2026-08-06 12:14:48.594072', 3, 9),
(24, '2026-08-06 12:14:48.594072', 4, 9),
(25, '2026-08-06 12:14:48.594072', 2, 10),
(26, '2026-08-06 12:14:48.594072', 3, 10),
(27, '2026-08-06 12:14:48.594072', 4, 10),
(28, '2026-08-06 12:14:48.594072', 3, 11),
(29, '2026-08-06 12:14:48.594072', 4, 11),
(30, '2026-08-06 12:14:48.594072', 4, 12),
(31, '2026-08-06 12:14:48.594072', 1, 13),
(32, '2026-08-06 12:14:48.594072', 2, 13),
(33, '2026-08-06 12:14:48.594072', 3, 13),
(34, '2026-08-06 12:14:48.594072', 4, 13),
(35, '2026-08-06 12:14:48.594072', 2, 14),
(36, '2026-08-06 12:14:48.594072', 3, 14),
(37, '2026-08-06 12:14:48.594072', 4, 14),
(38, '2026-08-06 12:14:48.594072', 3, 15),
(39, '2026-08-06 12:14:48.594072', 4, 15),
(40, '2026-08-06 12:14:48.594072', 4, 16),
(41, '2026-08-06 12:14:48.594072', 1, 17),
(42, '2026-08-06 12:14:48.594072', 2, 17),
(43, '2026-08-06 12:14:48.594072', 3, 17),
(44, '2026-08-06 12:14:48.594072', 4, 17),
(45, '2026-08-06 12:14:48.594072', 2, 18),
(46, '2026-08-06 12:14:48.594072', 3, 18),
(47, '2026-08-06 12:14:48.594072', 4, 18),
(48, '2026-08-06 12:14:48.594072', 3, 19),
(49, '2026-08-06 12:14:48.594072', 4, 19),
(50, '2026-08-06 12:14:48.594072', 4, 20),
(51, '2026-08-06 12:14:48.594072', 1, 21),
(52, '2026-08-06 12:14:48.594072', 2, 21),
(53, '2026-08-06 12:14:48.594072', 3, 21),
(54, '2026-08-06 12:14:48.594072', 4, 21),
(55, '2026-08-06 12:14:48.594072', 2, 22),
(56, '2026-08-06 12:14:48.594072', 3, 22),
(57, '2026-08-06 12:14:48.594072', 4, 22),
(58, '2026-08-06 12:14:48.594072', 3, 23),
(59, '2026-08-06 12:14:48.594072', 4, 23),
(60, '2026-08-06 12:14:48.594072', 4, 24),
(61, '2026-08-06 12:14:48.594072', 1, 25),
(62, '2026-08-06 12:14:48.594072', 2, 25),
(63, '2026-08-06 12:14:48.594072', 3, 25),
(64, '2026-08-06 12:14:48.594072', 4, 25),
(65, '2026-08-06 12:14:48.594072', 2, 26),
(66, '2026-08-06 12:14:48.594072', 3, 26),
(67, '2026-08-06 12:14:48.594072', 4, 26),
(68, '2026-08-06 12:14:48.594072', 3, 27),
(69, '2026-08-06 12:14:48.594072', 4, 27),
(70, '2026-08-06 12:14:48.594072', 4, 28),
(71, '2026-08-06 12:14:48.594072', 1, 29),
(72, '2026-08-06 12:14:48.594072', 2, 29),
(73, '2026-08-06 12:14:48.594072', 3, 29),
(74, '2026-08-06 12:14:48.594072', 4, 29),
(75, '2026-08-06 12:14:48.594072', 2, 30),
(76, '2026-08-06 12:14:48.594072', 3, 30),
(77, '2026-08-06 12:14:48.594072', 4, 30),
(78, '2026-08-06 12:14:48.594072', 3, 31),
(79, '2026-08-06 12:14:48.594072', 4, 31),
(80, '2026-08-06 12:14:48.594072', 4, 32),
(81, '2026-08-06 12:14:48.594072', 1, 33),
(82, '2026-08-06 12:14:48.594072', 2, 33),
(83, '2026-08-06 12:14:48.594072', 3, 33),
(84, '2026-08-06 12:14:48.594072', 4, 33),
(85, '2026-08-06 12:14:48.594072', 2, 34),
(86, '2026-08-06 12:14:48.594072', 3, 34),
(87, '2026-08-06 12:14:48.594072', 4, 34),
(88, '2026-08-06 12:14:48.594072', 3, 35),
(89, '2026-08-06 12:14:48.594072', 4, 35),
(90, '2026-08-06 12:14:48.594072', 4, 36),
(91, '2026-08-06 12:14:48.594072', 1, 37),
(92, '2026-08-06 12:14:48.594072', 2, 37),
(93, '2026-08-06 12:14:48.594072', 3, 37),
(94, '2026-08-06 12:14:48.594072', 4, 37),
(95, '2026-08-06 12:14:48.594072', 2, 38),
(96, '2026-08-06 12:14:48.594072', 3, 38),
(97, '2026-08-06 12:14:48.594072', 4, 38),
(98, '2026-08-06 12:14:48.594072', 3, 39),
(99, '2026-08-06 12:14:48.594072', 4, 39),
(100, '2026-08-06 12:14:48.594072', 4, 40),
(101, '2026-08-06 12:14:48.594072', 1, 41),
(102, '2026-08-06 12:14:48.594072', 2, 41),
(103, '2026-08-06 12:14:48.594072', 3, 41),
(104, '2026-08-06 12:14:48.594072', 4, 41),
(105, '2026-08-06 12:14:48.594072', 2, 42),
(106, '2026-08-06 12:14:48.594072', 3, 42),
(107, '2026-08-06 12:14:48.594072', 4, 42),
(108, '2026-08-06 12:14:48.594072', 3, 43),
(109, '2026-08-06 12:14:48.594072', 4, 43),
(110, '2026-08-06 12:14:48.594072', 4, 44),
(115, '2026-08-06 12:32:57.511693', 1, 2);

-- ============================================================================
-- ai_question_bookmark
-- ============================================================================
DROP TABLE IF EXISTS `ai_question_bookmark`;
CREATE TABLE `ai_question_bookmark` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `question_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_question_bookmark` (`user_id`,`question_id`),
  KEY `idx_ai_question_bookmark_user_id` (`user_id`),
  KEY `FKe12y9iryuh2b8md3qmv41gny9` (`question_id`),
  CONSTRAINT `FK47u5rhbkfdgteo2y222n21fdp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKe12y9iryuh2b8md3qmv41gny9` FOREIGN KEY (`question_id`) REFERENCES `ai_questions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2 row(s)
INSERT INTO `ai_question_bookmark` (`id`, `created_at`, `question_id`, `user_id`) VALUES
(1, '2026-08-06 12:19:50.533084', 1, 2),
(2, '2026-08-06 12:30:18.164662', 41, 2);

-- ============================================================================
-- ai_question_category
-- ============================================================================
DROP TABLE IF EXISTS `ai_question_category`;
CREATE TABLE `ai_question_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_question_category` (`question_id`,`category_id`),
  KEY `idx_ai_question_category_question_id` (`question_id`),
  KEY `idx_ai_question_category_category_id` (`category_id`),
  CONSTRAINT `FKd1jwfej7y6qlkyxbaxiihfri` FOREIGN KEY (`question_id`) REFERENCES `ai_questions` (`id`),
  CONSTRAINT `FKw3csadj5jdjhvbvo6b5dwfxr` FOREIGN KEY (`category_id`) REFERENCES `ai_categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 42 row(s)
INSERT INTO `ai_question_category` (`id`, `category_id`, `question_id`) VALUES
(1, 2, 1),
(2, 8, 1),
(3, 2, 2),
(4, 2, 3),
(5, 2, 4),
(6, 2, 5),
(7, 8, 6),
(8, 8, 7),
(9, 8, 8),
(10, 8, 9),
(11, 8, 10),
(12, 1, 11),
(13, 1, 12),
(14, 1, 13),
(15, 1, 14),
(16, 1, 15),
(17, 6, 16),
(18, 6, 17),
(19, 6, 18),
(20, 6, 19),
(21, 6, 20),
(22, 5, 21),
(23, 5, 22),
(24, 5, 23),
(25, 5, 24),
(26, 5, 25),
(27, 9, 26),
(28, 9, 27),
(29, 9, 28),
(30, 9, 29),
(31, 9, 30),
(32, 7, 31),
(33, 7, 32),
(34, 7, 33),
(35, 7, 34),
(36, 7, 35),
(37, 4, 36),
(38, 4, 37),
(39, 4, 38),
(40, 4, 39),
(41, 4, 40),
(43, 4, 41);

-- ============================================================================
-- ai_questions
-- ============================================================================
DROP TABLE IF EXISTS `ai_questions`;
CREATE TABLE `ai_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `detailed_answer` text NOT NULL,
  `difficulty_level` varchar(20) NOT NULL,
  `real_world_example` text,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `title` varchar(500) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 44 row(s)
INSERT INTO `ai_questions` (`id`, `created_at`, `detailed_answer`, `difficulty_level`, `real_world_example`, `status`, `title`, `updated_at`) VALUES
(1, '2026-08-06 12:14:48.092170', 'Dependency injection helps achieve loose coupling between components. In Spring Boot, the framework automatically creates and injects dependencies using annotations such as @Autowired, @Service, and @Component, instead of a class constructing its own collaborators with \'new\'.', 'EASY', 'A payment service receives a payment gateway object instead of creating it directly, so a test can inject a mock gateway without touching real payment infrastructure.', 'ACTIVE', 'What is dependency injection?', '2026-08-06 12:14:48.092170'),
(2, '2026-08-06 12:14:48.111165', 'HashMap is not thread-safe — concurrent reads and writes from multiple threads can corrupt its internal bucket structure or produce inconsistent results. ConcurrentHashMap achieves thread safety by partitioning the map into segments and using fine-grained locking (or CAS operations internally), allowing concurrent reads and a high degree of concurrent writes without locking the entire map.', 'MEDIUM', 'A shared in-memory cache accessed by multiple request-handling threads in a web server should use ConcurrentHashMap; using a plain HashMap there can cause rare, hard-to-reproduce data corruption under load.', 'ACTIVE', 'What is the difference between HashMap and ConcurrentHashMap?', '2026-08-06 12:14:48.111165'),
(3, '2026-08-06 12:14:48.122169', '== compares object references for objects (whether two variables point to the same memory location) and compares primitive values directly for primitives. equals() is a method that can be overridden to compare logical/content equality between two objects — the default Object.equals() implementation just falls back to ==.', 'EASY', 'Two separate String objects with the same text are \'==\' false but \'equals()\' true; comparing user-entered emails for a login check must use equals(), not ==.', 'ACTIVE', 'What is the difference between == and equals() in Java?', '2026-08-06 12:14:48.122169'),
(4, '2026-08-06 12:14:48.133168', 'Checked exceptions extend Exception (excluding RuntimeException) and must be either caught or declared in a method\'s throws clause — the compiler enforces handling. Unchecked exceptions extend RuntimeException and represent programming errors or unexpected conditions that the compiler does not force you to handle.', 'MEDIUM', 'IOException when reading a file is checked (the file might not exist, so callers must handle it), while NullPointerException from a missing null check is unchecked.', 'ACTIVE', 'What is the difference between checked and unchecked exceptions?', '2026-08-06 12:14:48.133168'),
(5, '2026-08-06 12:14:48.143168', 'The JVM\'s garbage collector reclaims objects that are no longer reachable from a set of GC roots (active thread stacks, static fields, JNI references). It never uses reference counting for this in modern collectors; instead it periodically traces reachability from those roots and frees everything not reached, often using generational collection to focus effort on short-lived objects.', 'HARD', 'A request-scoped object created inside a controller method becomes eligible for collection as soon as the method returns and nothing else still references it.', 'ACTIVE', 'How does the JVM garbage collector decide when to reclaim an object?', '2026-08-06 12:14:48.143168'),
(6, '2026-08-06 12:14:48.153168', 'All three are specializations of Spring\'s @Component and are detected the same way by component scanning, registering the class as a bean. The distinction is purely semantic/documentation: @Service marks business-logic classes, @Repository marks data-access classes (and additionally enables persistence-exception translation), and @Component is the generic catch-all.', 'EASY', 'A PaymentService class is annotated @Service, while the PaymentRepository interface backing it is annotated @Repository, making the architectural layering obvious to readers.', 'ACTIVE', 'What is the difference between @Component, @Service, and @Repository?', '2026-08-06 12:14:48.153168'),
(7, '2026-08-06 12:14:48.162711', 'Spring Boot scans the classpath at startup and, using @Conditional annotations (like @ConditionalOnClass or @ConditionalOnMissingBean), registers sensible default beans only when appropriate dependencies are present and the developer hasn\'t already defined their own bean of that type. This is driven by auto-configuration classes listed in a META-INF/spring/... imports file inside each starter jar.', 'MEDIUM', 'Adding spring-boot-starter-data-jpa to the classpath auto-configures a DataSource and EntityManagerFactory automatically, with zero manual bean definitions required.', 'ACTIVE', 'How does Spring Boot auto-configuration work?', '2026-08-06 12:14:48.162711'),
(8, '2026-08-06 12:14:48.171858', '@RequestParam extracts a value from the query string or form data (e.g. ?page=2), while @PathVariable extracts a value from a placeholder in the URI template itself (e.g. /users/{id}). Both can be made optional/required and support type conversion.', 'EASY', 'GET /users/42?includeOrders=true uses @PathVariable for 42 (the user id) and @RequestParam for includeOrders.', 'ACTIVE', 'What is the difference between @RequestParam and @PathVariable?', '2026-08-06 12:14:48.171858'),
(9, '2026-08-06 12:14:48.181855', 'By default, Spring beans are singleton-scoped: one instance per application context, created eagerly at startup unless marked lazy. Other scopes include prototype (a new instance per injection point) and web-specific scopes like request/session. Lifecycle callbacks (@PostConstruct, @PreDestroy, or InitializingBean/DisposableBean) let a bean run initialization or cleanup logic as the container creates or destroys it.', 'HARD', 'A shared JdbcTemplate bean is singleton-scoped (safe to reuse across requests), while a stateful per-request builder object might be declared prototype-scoped.', 'ACTIVE', 'Explain how Spring manages bean lifecycle and scopes.', '2026-08-06 12:14:48.181855'),
(10, '2026-08-06 12:14:48.192293', '@Transactional wraps the method call in a database transaction managed by Spring\'s transaction infrastructure (via an AOP proxy). If the method completes normally the transaction commits; if it throws an unchecked exception, the transaction rolls back automatically. Checked exceptions don\'t trigger rollback unless configured to.', 'MEDIUM', 'A \'transferFunds\' method that debits one account and credits another is annotated @Transactional so that, if the credit step fails, the earlier debit is rolled back too.', 'ACTIVE', 'What does @Transactional do when applied to a service method?', '2026-08-06 12:14:48.192293'),
(11, '2026-08-06 12:14:48.201629', 'SQS (Simple Queue Service) is a pull-based message queue: one or more consumers poll the queue and each message is typically processed by exactly one consumer. SNS (Simple Notification Service) is a push-based publish/subscribe service: a single published message can fan out to many subscribers (SQS queues, Lambda, email, HTTP endpoints) simultaneously.', 'MEDIUM', 'An order-placed event is published to an SNS topic, which fans out to an SQS queue for inventory processing and a separate SQS queue for sending confirmation emails.', 'ACTIVE', 'What is the difference between Amazon SQS and Amazon SNS?', '2026-08-06 12:14:48.201629'),
(12, '2026-08-06 12:14:48.209628', 'It splits security duties between AWS and the customer: AWS is responsible for the security \'of\' the cloud — physical data centers, host infrastructure, and the virtualization layer. The customer is responsible for security \'in\' the cloud — data encryption, IAM policies, network configuration, and OS/application patching on anything they control.', 'EASY', 'AWS patches the physical hypervisor, but a customer running EC2 instances is still responsible for patching the guest OS and configuring security groups correctly.', 'ACTIVE', 'What is the AWS Shared Responsibility Model?', '2026-08-06 12:14:48.209628'),
(13, '2026-08-06 12:14:48.227634', 'S3 is object storage accessed over HTTP(S), designed for durability and scale, and is not attached to a single instance — many instances or services can read the same objects concurrently. EBS is block storage attached to a single EC2 instance at a time (like a virtual hard disk), used for OS volumes and databases that need low-latency block-level access.', 'MEDIUM', 'Application logs and user-uploaded files are stored in S3, while the database data files for an EC2-hosted MySQL instance live on an attached EBS volume.', 'ACTIVE', 'Explain the difference between S3 and EBS.', '2026-08-06 12:14:48.227634'),
(14, '2026-08-06 12:14:48.237631', 'An Auto Scaling Group (ASG) automatically launches or terminates EC2 instances to keep the fleet size within a configured min/max range, scaling in response to metrics like CPU utilization or a schedule. It also replaces unhealthy instances automatically, improving both cost efficiency and availability.', 'MEDIUM', 'An e-commerce site configures an ASG to scale from 2 to 20 instances automatically during a flash sale, then scale back down afterward to control cost.', 'ACTIVE', 'What is an Auto Scaling Group and why is it used?', '2026-08-06 12:14:48.237631'),
(15, '2026-08-06 12:14:48.246629', 'Lambda runs your code in ephemeral, managed execution environments that AWS provisions on demand in response to a trigger (an API call, an S3 event, a queue message). You are billed only for actual execution time and never manage or provision the underlying servers; AWS handles scaling, patching, and availability.', 'HARD', 'An image-upload event on S3 triggers a Lambda function that generates a thumbnail, without any server having to run continuously waiting for uploads.', 'ACTIVE', 'How does AWS Lambda achieve serverless execution?', '2026-08-06 12:14:48.246629'),
(16, '2026-08-06 12:14:48.262072', 'ARM is the deployment and management layer for Azure: every operation (via portal, CLI, or SDK) goes through ARM, which lets resources be organized into resource groups, tagged, access-controlled with a single RBAC model, and deployed declaratively via ARM/Bicep templates for repeatable infrastructure-as-code.', 'MEDIUM', 'A team defines an ARM template describing a web app, its database, and its storage account together, so every environment (dev/staging/prod) is provisioned identically.', 'ACTIVE', 'What is Azure Resource Manager (ARM) and why is it important?', '2026-08-06 12:14:48.262072'),
(17, '2026-08-06 12:14:48.283071', 'Blob Storage stores unstructured object data (images, backups, logs) accessed over HTTP(S) via REST APIs or SDKs. Azure Files provides fully managed file shares accessible via the standard SMB or NFS protocols, so it can be mounted like a traditional network drive by both cloud and on-premises machines.', 'MEDIUM', 'A legacy Windows application that expects a mapped network drive uses Azure Files, while a web app storing user-uploaded photos uses Blob Storage.', 'ACTIVE', 'What is the difference between Azure Blob Storage and Azure Files?', '2026-08-06 12:14:48.283071'),
(18, '2026-08-06 12:14:48.296072', 'Availability Sets protect against hardware failure within a single data center by spreading VMs across different physical racks (fault domains) and host update groups (update domains). Availability Zones go further, spreading resources across physically separate data centers within a region, protecting against a full data-center-level outage, not just a rack failure.', 'HARD', 'A mission-critical service is deployed across three Availability Zones in a region so it survives an entire data center going offline.', 'ACTIVE', 'Explain Azure Availability Zones vs Availability Sets.', '2026-08-06 12:14:48.296072'),
(19, '2026-08-06 12:14:48.307072', 'It is Azure\'s cloud-based identity and access management service: it authenticates users, issues tokens for single sign-on across applications, manages role-based access control, and supports multi-factor authentication and conditional access policies.', 'EASY', 'Employees sign in once to Microsoft Entra ID and get single sign-on access to Microsoft 365, an internal HR portal, and third-party SaaS apps without re-entering credentials.', 'ACTIVE', 'What is Azure Active Directory (Microsoft Entra ID) used for?', '2026-08-06 12:14:48.307072'),
(20, '2026-08-06 12:14:48.318078', 'Azure Functions is event-driven, serverless compute: code runs only in response to a trigger and you\'re billed per execution (or on a fixed plan if chosen), with automatic scaling to zero when idle. Azure App Service hosts long-running web applications and APIs on an always-on (or scaled) set of instances that you provision and pay for continuously.', 'MEDIUM', 'A scheduled nightly report-generation job is a good fit for Azure Functions, while a customer-facing web application with constant traffic runs on App Service.', 'ACTIVE', 'How does Azure Functions differ from Azure App Service?', '2026-08-06 12:14:48.318078'),
(21, '2026-08-06 12:14:48.326077', 'A Pod is the smallest deployable unit in Kubernetes: one or more tightly coupled containers that share the same network namespace (IP address and port space) and can share storage volumes. Containers within a Pod are always scheduled together on the same node.', 'EASY', 'A main application container and a logging \'sidecar\' container that ships its logs are deployed together in the same Pod so they can communicate over localhost.', 'ACTIVE', 'What is a Kubernetes Pod?', '2026-08-06 12:14:48.326077'),
(22, '2026-08-06 12:14:48.335071', 'A Deployment manages a set of interchangeable, stateless Pod replicas — any replica can be replaced by an identical new one with a new random name/IP. A StatefulSet manages Pods that need a stable identity (a predictable, persistent name like pod-0, pod-1) and stable per-replica storage across restarts, which stateful workloads like databases require.', 'MEDIUM', 'A stateless REST API runs as a Deployment, while a 3-node Kafka or Elasticsearch cluster runs as a StatefulSet so each node keeps its own persistent volume and identity.', 'ACTIVE', 'Explain the difference between a Deployment and a StatefulSet.', '2026-08-06 12:14:48.335071'),
(23, '2026-08-06 12:14:48.344071', 'Pods are ephemeral and get new IP addresses whenever they\'re recreated, so a Service provides a stable virtual IP and DNS name that load-balances traffic across the current set of matching Pods (selected by labels), decoupling clients from any individual Pod\'s lifecycle.', 'EASY', 'A frontend Pod calls \'http://backend-service\' rather than a specific backend Pod\'s IP, so backend Pods can be replaced or scaled without breaking the frontend.', 'ACTIVE', 'What is a Kubernetes Service and why is it needed?', '2026-08-06 12:14:48.344071'),
(24, '2026-08-06 12:14:48.355071', 'The kubelet on each node continuously checks container health via configured liveness and readiness probes; a failing container is restarted automatically. At the cluster level, controllers (like the Deployment controller) constantly reconcile actual state against desired state, recreating Pods on healthy nodes if a node fails entirely.', 'MEDIUM', 'If a worker node crashes, the Pods it was running are rescheduled onto other healthy nodes automatically, without any manual intervention.', 'ACTIVE', 'How does Kubernetes handle self-healing?', '2026-08-06 12:14:48.355071'),
(25, '2026-08-06 12:14:48.367071', 'etcd is a distributed, consistent key-value store that serves as the single source of truth for all cluster state — every object (Pods, Services, ConfigMaps, and their desired state) is persisted there. The API server is the only component that talks to etcd directly; every other component reads/writes cluster state through it.', 'HARD', 'When you run \'kubectl apply\', the API server validates and writes the new desired state into etcd, and controllers watching etcd notice the change and act on it.', 'ACTIVE', 'What is the role of etcd in a Kubernetes cluster?', '2026-08-06 12:14:48.367071'),
(26, '2026-08-06 12:14:48.380072', 'An image is a read-only, immutable template — a packaged filesystem plus metadata describing how to run an application. A container is a running (or stopped) instance created from an image, with its own writable layer on top; you can start many independent containers from the same single image.', 'EASY', 'The same \'myapp:1.0\' image can be used to start ten separate containers for load testing, each with independent runtime state.', 'ACTIVE', 'What is the difference between a Docker image and a Docker container?', '2026-08-06 12:14:48.380072'),
(27, '2026-08-06 12:14:48.394073', 'Each instruction in a Dockerfile (FROM, RUN, COPY, etc.) creates a new, cached filesystem layer. Docker reuses cached layers for any instruction whose inputs haven\'t changed, so ordering instructions from least-to-most frequently changing (e.g. installing dependencies before copying application source) dramatically speeds up rebuilds.', 'MEDIUM', 'Putting \'COPY package.json\' and \'RUN npm install\' before \'COPY . .\' means dependency installation is skipped on rebuilds unless package.json itself changed.', 'ACTIVE', 'Explain Docker layers and why they matter for build performance.', '2026-08-06 12:14:48.394073'),
(28, '2026-08-06 12:14:48.407074', 'Multi-stage builds let you use one stage (with a full SDK/build toolchain) to compile or build an application, then copy only the resulting artifact into a much smaller final stage (e.g. a minimal JRE or alpine image), keeping the shipped image small and free of build-time tooling and source code.', 'MEDIUM', 'A Java app is compiled in a \'maven\' build stage, then only the resulting .jar is copied into a slim \'eclipse-temurin-jre\' final image, cutting the shipped image size drastically.', 'ACTIVE', 'What is the purpose of a Dockerfile\'s multi-stage build?', '2026-08-06 12:14:48.407074'),
(29, '2026-08-06 12:14:48.417080', 'By default, Docker creates a \'bridge\' network on the host; each container gets its own network namespace and a private IP on that bridge, and can reach other containers on the same bridge network by name via Docker\'s embedded DNS. Ports must be explicitly published (-p) to be reachable from outside the host.', 'HARD', 'Two containers on the same custom bridge network can reach each other as \'http://db:5432\' using the service name as a hostname, without any manual IP configuration.', 'ACTIVE', 'How does Docker networking work by default?', '2026-08-06 12:14:48.417080'),
(30, '2026-08-06 12:14:48.427071', 'ENTRYPOINT defines the fixed executable that always runs when the container starts, while CMD supplies default arguments that can be overridden at \'docker run\' time. When both are set, CMD\'s value is passed as arguments to ENTRYPOINT.', 'MEDIUM', 'ENTRYPOINT ["java", "-jar", "app.jar"] with CMD ["--spring.profiles.active=dev"] lets a user override just the profile at runtime without changing the fixed java command.', 'ACTIVE', 'What is the difference between CMD and ENTRYPOINT in a Dockerfile?', '2026-08-06 12:14:48.427071'),
(31, '2026-08-06 12:14:48.437070', 'INNER JOIN returns only rows that have a matching row in both tables. LEFT JOIN returns every row from the left table regardless of a match, filling in NULLs for columns from the right table when no match exists.', 'EASY', 'Listing \'all customers and their orders, including customers with zero orders\' requires a LEFT JOIN from customers to orders; an INNER JOIN would silently drop those customers.', 'ACTIVE', 'What is the difference between INNER JOIN and LEFT JOIN?', '2026-08-06 12:14:48.437070'),
(32, '2026-08-06 12:14:48.446071', 'Normalization is the process of organizing tables to reduce data redundancy and avoid update anomalies, typically by splitting data into smaller related tables (e.g. moving repeating customer-address data into its own table) and enforcing relationships via foreign keys, following forms like 1NF, 2NF, and 3NF.', 'MEDIUM', 'Storing a customer\'s address once in a \'customers\' table instead of repeating it on every row of an \'orders\' table prevents inconsistent addresses if the customer moves.', 'ACTIVE', 'Explain database normalization and why it matters.', '2026-08-06 12:14:48.446071'),
(33, '2026-08-06 12:14:48.455070', 'A clustered index determines the physical storage order of the table\'s rows — there can be only one per table, since rows can only be sorted one way on disk. A non-clustered index is a separate structure that stores pointers back to the actual rows, and a table can have many of them.', 'HARD', 'The primary key often backs the clustered index (rows physically ordered by id), while a non-clustered index on \'email\' speeds up login lookups without reordering the table.', 'ACTIVE', 'What is the difference between a clustered and non-clustered index?', '2026-08-06 12:14:48.455070'),
(34, '2026-08-06 12:14:48.463072', 'A transaction groups multiple operations so they succeed or fail as a single unit. ACID describes the guarantees: Atomicity (all-or-nothing), Consistency (the database moves between valid states), Isolation (concurrent transactions don\'t see each other\'s uncommitted changes), and Durability (committed changes survive a crash).', 'MEDIUM', 'Transferring money between two bank accounts debits one and credits the other inside a single transaction, so a crash midway never leaves money debited but not credited.', 'ACTIVE', 'What is a database transaction and what does ACID mean?', '2026-08-06 12:14:48.463072'),
(35, '2026-08-06 12:14:48.472071', 'The N+1 problem happens when code fetches a list of N parent records, then issues one additional query per parent to fetch related child data — resulting in N+1 total queries instead of a small, fixed number. It\'s commonly fixed with eager/batch fetching (e.g. a JOIN FETCH in JPQL) or explicit batch loading.', 'HARD', 'Fetching 100 orders and then querying each order\'s line items individually in a loop issues 101 queries; a single JOIN query (or a batched IN query) replaces all of them.', 'ACTIVE', 'Explain the N+1 query problem and how to avoid it.', '2026-08-06 12:14:48.472071'),
(36, '2026-08-06 12:14:48.480072', 'Orchestration uses a central coordinator that explicitly directs each service on what to do and in what order (a single point of control and visibility). Choreography has no central coordinator — each service reacts to events it observes and publishes its own events, so the overall flow emerges from independent, decentralized reactions.', 'HARD', 'A central \'OrderSaga\' service calling each step (reserve inventory, charge payment, ship) is orchestration; each service independently reacting to an \'OrderPlaced\' event on a message bus is choreography.', 'ACTIVE', 'What is the difference between orchestration and choreography in microservices?', '2026-08-06 12:14:48.480072'),
(37, '2026-08-06 12:14:48.490071', 'Classic two-phase-commit transactions across services are avoided because they don\'t scale and create tight coupling. Instead, microservices commonly use the Saga pattern: a sequence of local transactions, each publishing an event that triggers the next step, with explicit compensating actions to undo prior steps if a later step fails.', 'HARD', 'If payment fails after inventory was already reserved in an order Saga, a compensating \'ReleaseInventory\' step runs to undo the reservation.', 'ACTIVE', 'How do microservices typically handle distributed transactions?', '2026-08-06 12:14:48.490071'),
(38, '2026-08-06 12:14:48.499075', 'An API Gateway is a single entry point that sits in front of many microservices, handling cross-cutting concerns like authentication, rate limiting, request routing, and response aggregation, so individual services don\'t each need to reimplement them and clients don\'t need to know every service\'s address.', 'MEDIUM', 'A mobile app calls one gateway endpoint that internally fans out to the user, order, and inventory services and combines their responses into one payload.', 'ACTIVE', 'What is the API Gateway pattern?', '2026-08-06 12:14:48.499075'),
(39, '2026-08-06 12:14:48.508071', 'A circuit breaker wraps calls to a remote service and tracks failures; once failures exceed a threshold, it \'opens\' and short-circuits further calls immediately (failing fast or falling back) instead of letting requests pile up waiting on a struggling dependency. After a cooldown it allows a few trial requests through to see if the dependency has recovered.', 'MEDIUM', 'If the recommendation service is timing out, a circuit breaker stops calling it and instead returns a default \'no recommendations\' response, preventing the slowdown from cascading to the whole page.', 'ACTIVE', 'Explain the Circuit Breaker pattern and why it\'s needed.', '2026-08-06 12:14:48.508071'),
(40, '2026-08-06 12:14:48.518075', 'In a dynamic environment where service instances scale up/down and get new IPs constantly, service discovery lets services find each other\'s current network location at runtime via a registry, rather than relying on hardcoded addresses that would quickly become stale.', 'MEDIUM', 'A newly started \'inventory-service\' instance registers itself with the discovery registry, and the \'order-service\' looks up a healthy instance\'s address from that registry instead of a hardcoded IP.', 'ACTIVE', 'What is service discovery and why do microservices need it?', '2026-08-06 12:14:48.518075'),
(41, '2026-08-06 12:14:48.526072', 'The CAP theorem states that a distributed data store can only guarantee two of three properties at the same time during a network partition: Consistency (every read sees the latest write), Availability (every request gets a non-error response), and Partition tolerance (the system keeps working despite network splits). Since partitions are unavoidable in practice, real systems choose to favor either consistency (CP) or availability (AP) when a partition occurs.', 'HARD', 'A globally distributed shopping cart service might favor availability (AP) — always accept cart updates even if some replicas are briefly out of sync — over strict consistency.', 'ACTIVE', 'Explain the CAP theorem.', '2026-08-06 12:14:48.526072'),
(42, '2026-08-06 12:14:48.541072', 'Vertical scaling (\'scaling up\') means adding more resources (CPU, RAM) to a single existing machine, which has a hard ceiling and a single point of failure. Horizontal scaling (\'scaling out\') means adding more machines that share the load, which scales further and improves fault tolerance but requires the application to support running multiple instances (e.g. being stateless).', 'EASY', 'Upgrading a database server to a bigger instance type is vertical scaling; adding more web server instances behind a load balancer is horizontal scaling.', 'ACTIVE', 'What is the difference between horizontal and vertical scaling?', '2026-08-06 12:14:48.541072'),
(43, '2026-08-06 12:14:48.550077', 'A load balancer distributes incoming traffic across multiple backend servers to avoid overloading any single one and to improve availability. Common algorithms include round robin (rotate evenly), least connections (send to the least-busy server), and weighted variants that account for servers with different capacities.', 'MEDIUM', 'A load balancer in front of five identical API servers uses round robin so each server receives roughly a fifth of the traffic.', 'ACTIVE', 'What is a load balancer and what algorithms does it use?', '2026-08-06 12:14:48.550077'),
(44, '2026-08-06 12:14:48.559072', 'A CDN caches static (and sometimes dynamic) content on servers (\'edge locations\') geographically close to end users, so requests are served from a nearby edge instead of traveling all the way to the origin server, reducing latency and offloading traffic from the origin.', 'EASY', 'A user in Mumbai downloading a video hosted on a US origin server instead gets it from a CDN edge node in Mumbai, cutting load time dramatically.', 'ACTIVE', 'How does a Content Delivery Network (CDN) improve performance?', '2026-08-06 12:14:48.559072');

-- ============================================================================
-- exams
-- ============================================================================
DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4 row(s)
INSERT INTO `exams` (`id`, `name`, `category`, `description`) VALUES
(1, 'Core Java', 'Java', 'Java language fundamentals, OOP, and collections for interview prep.'),
(2, 'Spring Boot', 'Spring Boot', 'Spring Boot, Spring MVC, Spring Data JPA, and Spring Security essentials.'),
(3, 'AWS Cloud Practitioner', 'AWS', 'Core AWS services, pricing, and architecture for certification prep.'),
(4, 'Azure Fundamentals', 'Azure', 'Core Azure services, governance, and pricing for AZ-900 style prep.');

-- ============================================================================
-- payment_order
-- ============================================================================
DROP TABLE IF EXISTS `payment_order`;
CREATE TABLE `payment_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `plan_id` bigint NOT NULL,
  `razorpay_order_id` varchar(100) NOT NULL,
  `amount` bigint NOT NULL,
  `currency` varchar(10) NOT NULL,
  `status` varchar(20) NOT NULL,
  `receipt` varchar(100) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_order_razorpay_order_id` (`razorpay_order_id`),
  KEY `fk_payment_order_plan` (`plan_id`),
  KEY `idx_payment_order_user_plan_status_created` (`user_id`,`plan_id`,`status`,`created_at`),
  CONSTRAINT `fk_payment_order_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`),
  CONSTRAINT `fk_payment_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7 row(s)
INSERT INTO `payment_order` (`id`, `user_id`, `plan_id`, `razorpay_order_id`, `amount`, `currency`, `status`, `receipt`, `created_at`, `updated_at`) VALUES
(1, 2, 2, 'order_TMRxwkGBAZMy3a', 9900, 'INR', 'PAID', 'rcpt_u2_p2_1786011880532', '2026-08-06 10:24:40.840980', '2026-08-06 10:26:21.623280'),
(2, 2, 2, 'order_TMS02ADtl54qNy', 9900, 'INR', 'PAID', 'rcpt_u2_p2_1786011999296', '2026-08-06 10:26:39.385020', '2026-08-06 10:27:01.181299'),
(3, 2, 3, 'order_TMSwn38zOqCbk8', 29900, 'INR', 'PAID', 'rcpt_u2_p3_1786015336392', '2026-08-06 11:22:16.697117', '2026-08-06 11:22:56.560380'),
(4, 2, 4, 'order_TMT0CL03qDgr12', 99900, 'INR', 'CREATED', 'rcpt_u2_p4_1786015530140', '2026-08-06 11:25:30.242355', '2026-08-06 11:25:30.242355'),
(5, 2, 2, 'order_TMT1338Z1Q5XoM', 9900, 'INR', 'PAID', 'rcpt_u2_p2_1786015578445', '2026-08-06 11:26:18.521301', '2026-08-06 11:26:50.384580'),
(6, 2, 3, 'order_TMT23NakRknfqM', 29900, 'INR', 'PAID', 'rcpt_u2_p3_1786015635544', '2026-08-06 11:27:15.623166', '2026-08-06 11:27:39.495813'),
(7, 2, 4, 'order_TMVv22FnRF1Jyb', 99900, 'INR', 'CREATED', 'rcpt_u2_p4_1786025800667', '2026-08-06 14:16:41.458200', '2026-08-06 14:16:41.458200');

-- ============================================================================
-- payment_transaction
-- ============================================================================
DROP TABLE IF EXISTS `payment_transaction`;
CREATE TABLE `payment_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  `razorpay_payment_id` varchar(100) NOT NULL,
  `razorpay_signature` varchar(255) NOT NULL,
  `status` varchar(20) NOT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_transaction_razorpay_payment_id` (`razorpay_payment_id`),
  KEY `fk_payment_transaction_user` (`user_id`),
  KEY `fk_payment_transaction_order` (`order_id`),
  CONSTRAINT `fk_payment_transaction_order` FOREIGN KEY (`order_id`) REFERENCES `payment_order` (`id`),
  CONSTRAINT `fk_payment_transaction_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5 row(s)
INSERT INTO `payment_transaction` (`id`, `user_id`, `order_id`, `razorpay_payment_id`, `razorpay_signature`, `status`, `payment_method`, `created_at`, `updated_at`) VALUES
(1, 2, 1, 'pay_TMRzOGuWYSb4xj', '7d61ff6c0f91a7d67c3bbfef972de35bfa0a0842d67779b268963f5cec22121e', 'SUCCESS', 'wallet', '2026-08-06 10:26:21.121688', '2026-08-06 10:26:21.121688'),
(2, 2, 2, 'pay_TMS09UjsFYAi7q', '7391b1a1a342d969f6fbb57fe829de94fa171b5503d6e8803d71a0487c6a8028', 'SUCCESS', 'wallet', '2026-08-06 10:27:00.582801', '2026-08-06 10:27:00.582801'),
(3, 2, 3, 'pay_TMSxC0e9LQRbUl', '6595ff5a550586c21d5ecbc4a112cce5cf8e7ebd5ad8655022e95fea66ba2630', 'SUCCESS', 'wallet', '2026-08-06 11:22:56.073868', '2026-08-06 11:22:56.073868'),
(4, 2, 5, 'pay_TMT1J87p5L5F7r', '39d110a1195ceaeb77a2623952770b5c65222eb4bd6070c507e04a7000298264', 'SUCCESS', 'wallet', '2026-08-06 11:26:49.724843', '2026-08-06 11:26:49.724843'),
(5, 2, 6, 'pay_TMT2C9xFNyfjxK', '4d53c40fd63568f9ea6f431bdc33dae96b88babaeb7125332f0f8272d58f3731', 'SUCCESS', 'wallet', '2026-08-06 11:27:38.970378', '2026-08-06 11:27:38.970378');

-- ============================================================================
-- plan_question
-- ============================================================================
DROP TABLE IF EXISTS `plan_question`;
CREATE TABLE `plan_question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_question_plan_id_question_id` (`plan_id`,`question_id`),
  KEY `idx_plan_question_plan_id` (`plan_id`),
  KEY `idx_plan_question_question_id` (`question_id`),
  CONSTRAINT `fk_plan_question_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`),
  CONSTRAINT `fk_plan_question_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 100 row(s)
INSERT INTO `plan_question` (`id`, `plan_id`, `question_id`, `created_at`) VALUES
(1, 1, 1, '2026-08-06 09:17:23.408572'),
(2, 2, 1, '2026-08-06 09:17:23.408572'),
(3, 3, 1, '2026-08-06 09:17:23.408572'),
(4, 4, 1, '2026-08-06 09:17:23.408572'),
(5, 2, 2, '2026-08-06 09:17:23.408572'),
(6, 3, 2, '2026-08-06 09:17:23.408572'),
(7, 4, 2, '2026-08-06 09:17:23.408572'),
(8, 3, 3, '2026-08-06 09:17:23.408572'),
(9, 4, 3, '2026-08-06 09:17:23.408572'),
(10, 4, 4, '2026-08-06 09:17:23.408572'),
(11, 1, 5, '2026-08-06 09:17:23.408572'),
(12, 2, 5, '2026-08-06 09:17:23.408572'),
(13, 3, 5, '2026-08-06 09:17:23.408572'),
(14, 4, 5, '2026-08-06 09:17:23.408572'),
(15, 2, 6, '2026-08-06 09:17:23.408572'),
(16, 3, 6, '2026-08-06 09:17:23.408572'),
(17, 4, 6, '2026-08-06 09:17:23.408572'),
(18, 3, 7, '2026-08-06 09:17:23.408572'),
(19, 4, 7, '2026-08-06 09:17:23.408572'),
(20, 4, 8, '2026-08-06 09:17:23.408572'),
(21, 1, 9, '2026-08-06 09:17:23.408572'),
(22, 2, 9, '2026-08-06 09:17:23.408572'),
(23, 3, 9, '2026-08-06 09:17:23.408572'),
(24, 4, 9, '2026-08-06 09:17:23.408572'),
(25, 2, 10, '2026-08-06 09:17:23.408572'),
(26, 3, 10, '2026-08-06 09:17:23.408572'),
(27, 4, 10, '2026-08-06 09:17:23.408572'),
(28, 3, 11, '2026-08-06 09:17:23.408572'),
(29, 4, 11, '2026-08-06 09:17:23.408572'),
(30, 4, 12, '2026-08-06 09:17:23.408572'),
(31, 1, 13, '2026-08-06 09:17:23.408572'),
(32, 2, 13, '2026-08-06 09:17:23.408572'),
(33, 3, 13, '2026-08-06 09:17:23.408572'),
(34, 4, 13, '2026-08-06 09:17:23.408572'),
(35, 2, 14, '2026-08-06 09:17:23.408572'),
(36, 3, 14, '2026-08-06 09:17:23.408572'),
(37, 4, 14, '2026-08-06 09:17:23.408572'),
(38, 3, 15, '2026-08-06 09:17:23.408572'),
(39, 4, 15, '2026-08-06 09:17:23.408572'),
(40, 4, 16, '2026-08-06 09:17:23.408572'),
(41, 1, 17, '2026-08-06 09:17:23.408572'),
(42, 2, 17, '2026-08-06 09:17:23.408572'),
(43, 3, 17, '2026-08-06 09:17:23.408572'),
(44, 4, 17, '2026-08-06 09:17:23.408572'),
(45, 2, 18, '2026-08-06 09:17:23.408572'),
(46, 3, 18, '2026-08-06 09:17:23.408572'),
(47, 4, 18, '2026-08-06 09:17:23.408572'),
(48, 3, 19, '2026-08-06 09:17:23.408572'),
(49, 4, 19, '2026-08-06 09:17:23.408572'),
(50, 4, 20, '2026-08-06 09:17:23.408572'),
(51, 1, 21, '2026-08-06 09:17:23.408572'),
(52, 2, 21, '2026-08-06 09:17:23.408572'),
(53, 3, 21, '2026-08-06 09:17:23.408572'),
(54, 4, 21, '2026-08-06 09:17:23.408572'),
(55, 2, 22, '2026-08-06 09:17:23.408572'),
(56, 3, 22, '2026-08-06 09:17:23.408572'),
(57, 4, 22, '2026-08-06 09:17:23.408572'),
(58, 3, 23, '2026-08-06 09:17:23.408572'),
(59, 4, 23, '2026-08-06 09:17:23.408572'),
(60, 4, 24, '2026-08-06 09:17:23.408572'),
(61, 1, 25, '2026-08-06 09:17:23.408572'),
(62, 2, 25, '2026-08-06 09:17:23.408572'),
(63, 3, 25, '2026-08-06 09:17:23.408572'),
(64, 4, 25, '2026-08-06 09:17:23.408572'),
(65, 2, 26, '2026-08-06 09:17:23.408572'),
(66, 3, 26, '2026-08-06 09:17:23.408572'),
(67, 4, 26, '2026-08-06 09:17:23.408572'),
(68, 3, 27, '2026-08-06 09:17:23.408572'),
(69, 4, 27, '2026-08-06 09:17:23.408572'),
(70, 4, 28, '2026-08-06 09:17:23.408572'),
(71, 1, 29, '2026-08-06 09:17:23.408572'),
(72, 2, 29, '2026-08-06 09:17:23.408572'),
(73, 3, 29, '2026-08-06 09:17:23.408572'),
(74, 4, 29, '2026-08-06 09:17:23.408572'),
(75, 2, 30, '2026-08-06 09:17:23.408572'),
(76, 3, 30, '2026-08-06 09:17:23.408572'),
(77, 4, 30, '2026-08-06 09:17:23.408572'),
(78, 3, 31, '2026-08-06 09:17:23.408572'),
(79, 4, 31, '2026-08-06 09:17:23.408572'),
(80, 4, 32, '2026-08-06 09:17:23.408572'),
(81, 1, 33, '2026-08-06 09:17:23.408572'),
(82, 2, 33, '2026-08-06 09:17:23.408572'),
(83, 3, 33, '2026-08-06 09:17:23.408572'),
(84, 4, 33, '2026-08-06 09:17:23.408572'),
(85, 2, 34, '2026-08-06 09:17:23.408572'),
(86, 3, 34, '2026-08-06 09:17:23.408572'),
(87, 4, 34, '2026-08-06 09:17:23.408572'),
(88, 3, 35, '2026-08-06 09:17:23.408572'),
(89, 4, 35, '2026-08-06 09:17:23.408572'),
(90, 4, 36, '2026-08-06 09:17:23.408572'),
(91, 1, 37, '2026-08-06 09:17:23.408572'),
(92, 2, 37, '2026-08-06 09:17:23.408572'),
(93, 3, 37, '2026-08-06 09:17:23.408572'),
(94, 4, 37, '2026-08-06 09:17:23.408572'),
(95, 2, 38, '2026-08-06 09:17:23.408572'),
(96, 3, 38, '2026-08-06 09:17:23.408572'),
(97, 4, 38, '2026-08-06 09:17:23.408572'),
(98, 3, 39, '2026-08-06 09:17:23.408572'),
(99, 4, 39, '2026-08-06 09:17:23.408572'),
(100, 4, 40, '2026-08-06 09:17:23.408572');

-- ============================================================================
-- questions
-- ============================================================================
DROP TABLE IF EXISTS `questions`;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL,
  `question` varchar(2000) NOT NULL,
  `optiona` varchar(1000) NOT NULL,
  `optionb` varchar(1000) NOT NULL,
  `optionc` varchar(1000) NOT NULL,
  `optiond` varchar(1000) NOT NULL,
  `answer` varchar(10) NOT NULL,
  `explanation` varchar(2000) DEFAULT NULL,
  `difficulty` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_questions_exam` (`exam_id`),
  CONSTRAINT `fk_questions_exam` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 40 row(s)
INSERT INTO `questions` (`id`, `exam_id`, `question`, `optiona`, `optionb`, `optionc`, `optiond`, `answer`, `explanation`, `difficulty`) VALUES
(1, 1, 'Which keyword is used to prevent a class from being subclassed in Java?', 'static', 'final', 'const', 'sealed', 'B', '\'final\' on a class prevents any other class from extending it.', 'MEDIUM'),
(2, 1, 'What is the default value of a boolean instance variable in Java?', 'true', 'false', 'null', '0', 'B', 'Uninitialized boolean fields default to false.', 'MEDIUM'),
(3, 1, 'Which collection type does NOT allow duplicate elements?', 'ArrayList', 'LinkedList', 'HashSet', 'PriorityQueue', 'C', 'Set implementations like HashSet enforce uniqueness of elements.', 'MEDIUM'),
(4, 1, 'Which of these is a checked exception in Java?', 'NullPointerException', 'ArithmeticException', 'IOException', 'ArrayIndexOutOfBoundsException', 'C', 'IOException extends Exception directly and must be declared or caught; the others are unchecked RuntimeExceptions.', 'MEDIUM'),
(5, 1, 'What does the \'volatile\' keyword guarantee for a field?', 'Thread-safety for compound operations', 'Visibility of writes across threads', 'Atomicity of increment operations', 'Immutability', 'B', '\'volatile\' ensures visibility — reads always see the latest write — but does not make compound operations atomic.', 'MEDIUM'),
(6, 1, 'Which interface must a class implement to be used as a key in a HashMap reliably?', 'Comparable', 'Serializable', 'equals()/hashCode() contract', 'Cloneable', 'C', 'HashMap relies on a consistent equals()/hashCode() implementation to locate buckets and detect key equality.', 'MEDIUM'),
(7, 1, 'What is the time complexity of retrieving an element from an ArrayList by index?', 'O(1)', 'O(log n)', 'O(n)', 'O(n log n)', 'A', 'ArrayList is backed by an array, so indexed access is constant time.', 'MEDIUM'),
(8, 1, 'Which Java feature allows a lambda expression to access an enclosing local variable?', 'Any local variable', 'Only static variables', 'Effectively final local variables', 'Only instance fields', 'C', 'Lambdas can capture local variables only if they are final or effectively final.', 'MEDIUM'),
(9, 1, 'What does the Stream API\'s \'reduce\' operation do?', 'Filters elements matching a predicate', 'Combines elements into a single result using an accumulator', 'Sorts the stream', 'Skips duplicate elements', 'B', 'reduce() folds the stream elements into one result via a combining function.', 'MEDIUM'),
(10, 1, 'Which of these correctly describes Java\'s garbage collection?', 'Manual memory deallocation via free()', 'Automatic reclamation of unreachable objects', 'Only runs when JVM exits', 'Requires explicit destructor calls', 'B', 'The JVM\'s garbage collector automatically reclaims memory for objects no longer reachable from GC roots.', 'MEDIUM'),
(11, 2, 'Which annotation marks a class as a Spring Boot application entry point?', '@Component', '@SpringBootApplication', '@Configuration', '@Service', 'B', '@SpringBootApplication combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.', 'MEDIUM'),
(12, 2, 'What is the purpose of Spring Boot\'s auto-configuration?', 'To require XML config for every bean', 'To automatically configure beans based on classpath contents and properties', 'To disable all default beans', 'To replace dependency injection', 'B', 'Auto-configuration inspects the classpath and existing beans to wire up sensible defaults automatically.', 'MEDIUM'),
(13, 2, 'Which annotation is used to inject a bean by type in Spring?', '@Inject only', '@Autowired', '@Resource only', '@Bean', 'B', '@Autowired is Spring\'s primary annotation for dependency injection by type.', 'MEDIUM'),
(14, 2, 'In Spring Data JPA, what does \'ddl-auto: update\' do?', 'Drops and recreates the schema on every start', 'Updates the schema to match entities without dropping existing data', 'Disables schema generation', 'Validates schema only', 'B', '\'update\' incrementally alters the schema to match entity mappings, preserving existing data.', 'MEDIUM'),
(15, 2, 'Which Spring Security component authenticates incoming requests once per request in a filter chain?', 'DispatcherServlet', 'OncePerRequestFilter', 'ViewResolver', 'HandlerMapping', 'B', 'OncePerRequestFilter guarantees a single execution per request, commonly used for JWT authentication filters.', 'MEDIUM'),
(16, 2, 'What HTTP status does @ResponseStatus(HttpStatus.CREATED) typically pair with?', 'GET requests fetching a resource', 'POST requests that successfully create a resource', 'DELETE requests', 'PUT requests updating a resource', 'B', '201 Created is conventionally returned after a successful resource-creating POST.', 'MEDIUM'),
(17, 2, 'What does @Transactional do when applied to a service method?', 'Runs the method in a separate thread', 'Wraps the method in a database transaction, rolling back on runtime exceptions', 'Caches the method result', 'Logs method execution time', 'B', '@Transactional demarcates a transactional boundary and rolls back on unchecked exceptions by default.', 'MEDIUM'),
(18, 2, 'Which starter would you add to expose actuator health/metrics endpoints?', 'spring-boot-starter-actuator', 'spring-boot-starter-web', 'spring-boot-starter-test', 'spring-boot-starter-security', 'A', 'spring-boot-starter-actuator adds production-ready monitoring endpoints like /actuator/health.', 'MEDIUM'),
(19, 2, 'What is the primary benefit of constructor injection over field injection?', 'Faster runtime performance', 'Enables immutable dependencies and easier testing', 'Requires less code', 'Works only with @Autowired', 'B', 'Constructor injection allows final fields and makes required dependencies explicit and easily mockable in tests.', 'MEDIUM'),
(20, 2, 'Which annotation restricts an endpoint to users with a specific role?', '@RequestMapping', '@PreAuthorize("hasRole(\'ADMIN\')")', '@Valid', '@ResponseBody', 'B', '@PreAuthorize with a SpEL role expression enforces method-level role-based access control.', 'MEDIUM'),
(21, 3, 'Which AWS service provides object storage?', 'EBS', 'S3', 'EFS', 'RDS', 'B', 'Amazon S3 is AWS\'s scalable object storage service.', 'MEDIUM'),
(22, 3, 'What is the AWS shared responsibility model primarily about?', 'AWS handles all security', 'Customer handles all security', 'Security responsibilities split between AWS and the customer', 'There is no shared responsibility in AWS', 'C', 'AWS secures the cloud infrastructure; customers secure what they configure and store in the cloud.', 'MEDIUM'),
(23, 3, 'Which service is used for serverless compute in AWS?', 'EC2', 'Lambda', 'Lightsail', 'Elastic Beanstalk', 'B', 'AWS Lambda runs code in response to events without provisioning or managing servers.', 'MEDIUM'),
(24, 3, 'Which AWS service provides a managed relational database?', 'DynamoDB', 'RDS', 'S3', 'SQS', 'B', 'Amazon RDS manages relational databases like MySQL, PostgreSQL, and Aurora.', 'MEDIUM'),
(25, 3, 'What does an IAM policy define?', 'Network routing rules', 'Permissions for actions on AWS resources', 'Billing thresholds', 'EC2 instance types', 'B', 'IAM policies are JSON documents that define allowed or denied actions on AWS resources.', 'MEDIUM'),
(26, 3, 'Which AWS service is best suited for decoupling application components via message queues?', 'SNS', 'SQS', 'CloudFront', 'Route 53', 'B', 'Amazon SQS provides fully managed message queuing for decoupling microservices.', 'MEDIUM'),
(27, 3, 'What is the purpose of an Availability Zone?', 'A billing region', 'An isolated data center location within an AWS Region for fault tolerance', 'A type of IAM role', 'A CDN edge location', 'B', 'Availability Zones are physically separate data centers within a Region, used for high availability.', 'MEDIUM'),
(28, 3, 'Which pricing model offers the largest discount for steady-state, predictable workloads?', 'On-Demand', 'Spot Instances', 'Reserved Instances / Savings Plans', 'Free Tier', 'C', 'Reserved Instances and Savings Plans offer significant discounts in exchange for a usage commitment.', 'MEDIUM'),
(29, 3, 'Which AWS service delivers content to users with low latency via edge locations?', 'CloudFront', 'VPC', 'IAM', 'CloudTrail', 'A', 'Amazon CloudFront is AWS\'s content delivery network (CDN).', 'MEDIUM'),
(30, 3, 'Which service records API calls made within an AWS account for auditing?', 'CloudWatch', 'CloudTrail', 'Config', 'Inspector', 'B', 'AWS CloudTrail logs API activity across an account for governance and auditing.', 'MEDIUM'),
(31, 4, 'What is Azure Resource Manager (ARM) primarily used for?', 'Billing invoices only', 'Deploying, managing, and organizing Azure resources as a group', 'Monitoring network traffic', 'User authentication only', 'B', 'ARM is the deployment and management layer for creating, updating, and organizing Azure resources.', 'MEDIUM'),
(32, 4, 'Which Azure service provides scalable object storage?', 'Azure Blob Storage', 'Azure SQL Database', 'Azure Functions', 'Azure DevOps', 'A', 'Azure Blob Storage is Microsoft\'s object storage solution for unstructured data.', 'MEDIUM'),
(33, 4, 'What is the purpose of Azure Active Directory (Microsoft Entra ID)?', 'Object storage', 'Identity and access management', 'Virtual networking', 'Container orchestration', 'B', 'Azure AD (Microsoft Entra ID) is Azure\'s cloud-based identity and access management service.', 'MEDIUM'),
(34, 4, 'Which Azure compute service is fully serverless (event-driven, no server management)?', 'Azure Virtual Machines', 'Azure Functions', 'Azure Kubernetes Service', 'Azure Batch', 'B', 'Azure Functions runs event-triggered code without requiring the user to manage servers.', 'MEDIUM'),
(35, 4, 'What does an Azure Resource Group represent?', 'A billing currency', 'A logical container for resources sharing the same lifecycle', 'A network security rule', 'A VM image', 'B', 'Resource Groups group related resources for unified lifecycle management, access control, and billing.', 'MEDIUM'),
(36, 4, 'Which Azure pricing tool estimates the cost of Azure products before deployment?', 'Azure Monitor', 'Azure Pricing Calculator', 'Azure Advisor', 'Azure Policy', 'B', 'The Azure Pricing Calculator estimates the cost of Azure services based on configuration.', 'MEDIUM'),
(37, 4, 'Which service provides Infrastructure as a Service (IaaS) virtual machines in Azure?', 'Azure App Service', 'Azure Virtual Machines', 'Azure Logic Apps', 'Azure Cognitive Services', 'B', 'Azure Virtual Machines is Azure\'s core IaaS offering for provisioning VMs.', 'MEDIUM'),
(38, 4, 'What is the main benefit of Azure Availability Zones?', 'Lower storage cost only', 'Protection against datacenter-level failures within a region', 'Faster DNS resolution', 'Simplified billing', 'B', 'Availability Zones are physically separate locations within an Azure region that protect against datacenter failures.', 'MEDIUM'),
(39, 4, 'Which Azure governance feature enforces organizational rules on resource properties?', 'Azure Policy', 'Azure Monitor', 'Azure Backup', 'Azure Bastion', 'A', 'Azure Policy evaluates resources for compliance with defined rules and can enforce or audit them.', 'MEDIUM'),
(40, 4, 'What is the Azure Service Level Agreement (SLA) used for?', 'Defining CPU architecture', 'Microsoft\'s guaranteed uptime/performance commitment for a service', 'Setting resource group names', 'Configuring VNets', 'B', 'An SLA defines Microsoft\'s guaranteed performance and uptime commitments for a given Azure service.', 'MEDIUM');

-- ============================================================================
-- subscription_plan
-- ============================================================================
DROP TABLE IF EXISTS `subscription_plan`;
CREATE TABLE `subscription_plan` (
  `plan_id` bigint NOT NULL AUTO_INCREMENT,
  `plan_name` varchar(100) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `duration_in_months` int NOT NULL,
  `question_limit` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5 row(s)
INSERT INTO `subscription_plan` (`plan_id`, `plan_name`, `price`, `duration_in_months`, `question_limit`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Free', 0.00, 12, 50, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(2, 'Basic', 99.00, 12, 500, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(3, 'Premium', 299.00, 12, 2000, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(4, 'Enterprise', 999.00, 12, 10000, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(5, 'MigrationCheck', 49.00, 6, 300, 'ACTIVE', '2026-08-06 10:03:41.137663', '2026-08-06 10:03:41.137663');

-- ============================================================================
-- test_answers
-- ============================================================================
DROP TABLE IF EXISTS `test_answers`;
CREATE TABLE `test_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `test_attempt_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `selected_option` varchar(10) DEFAULT NULL,
  `correct` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_test_answers_test_attempt` (`test_attempt_id`),
  KEY `fk_test_answers_question` (`question_id`),
  CONSTRAINT `fk_test_answers_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`),
  CONSTRAINT `fk_test_answers_test_attempt` FOREIGN KEY (`test_attempt_id`) REFERENCES `tests` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 22 row(s)
INSERT INTO `test_answers` (`id`, `test_attempt_id`, `question_id`, `selected_option`, `correct`) VALUES
(1, 1, 10, 'B', 1),
(2, 1, 1, 'B', 1),
(3, 3, 19, 'B', 1),
(4, 3, 14, 'A', 0),
(5, 3, 17, 'B', 1),
(6, 3, 18, 'B', 0),
(7, 3, 20, 'B', 1),
(8, 3, 13, 'B', 1),
(9, 3, 15, 'C', 0),
(10, 3, 12, 'C', 0),
(11, 3, 16, 'C', 0),
(12, 3, 11, 'C', 0),
(13, 19, 9, 'B', 1),
(14, 19, 6, 'B', 0),
(15, 19, 10, 'C', 0),
(16, 19, 7, 'D', 0),
(17, 19, 3, 'B', 0),
(18, 19, 2, 'B', 1),
(19, 19, 5, 'B', 1),
(20, 19, 4, 'C', 1),
(21, 19, 8, 'C', 1),
(22, 19, 1, 'D', 0);

-- ============================================================================
-- tests
-- ============================================================================
DROP TABLE IF EXISTS `tests`;
CREATE TABLE `tests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `score` int DEFAULT NULL,
  `total_questions` int NOT NULL,
  `duration_seconds` int DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `started_at` timestamp(6) NOT NULL,
  `completed_at` timestamp(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_tests_exam` (`exam_id`),
  KEY `idx_tests_user_id_started_at` (`user_id`,`started_at`),
  KEY `idx_tests_status_score` (`status`,`score`),
  CONSTRAINT `fk_tests_exam` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`),
  CONSTRAINT `fk_tests_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 21 row(s)
INSERT INTO `tests` (`id`, `user_id`, `exam_id`, `score`, `total_questions`, `duration_seconds`, `status`, `started_at`, `completed_at`) VALUES
(1, 2, 1, 20, 10, 120, 'COMPLETED', '2026-08-06 10:03:07.569731', '2026-08-06 10:04:06.540360'),
(2, 2, 2, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 10:27:18.620779', NULL),
(3, 2, 2, 40, 10, 42, 'COMPLETED', '2026-08-06 10:27:18.620779', '2026-08-06 10:28:01.022332'),
(4, 2, 4, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 11:46:57.014619', NULL),
(5, 2, 4, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 11:46:57.014619', NULL),
(6, 2, 4, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 11:47:37.993191', NULL),
(7, 2, 4, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 11:47:37.995190', NULL),
(8, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 14:46:59.078100', NULL),
(9, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 14:46:59.078100', NULL),
(10, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 14:48:01.823620', NULL),
(11, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 14:48:01.886175', NULL),
(12, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 15:01:19.581186', NULL),
(13, 2, 1, NULL, 10, 20, 'INCOMPLETE', '2026-08-06 15:01:19.580187', '2026-08-06 15:09:39.058453'),
(14, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 15:03:27.417487', NULL),
(15, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 15:03:27.417487', NULL),
(16, 2, 1, NULL, 10, 17, 'INCOMPLETE', '2026-08-06 15:08:19.512662', '2026-08-06 15:08:36.956739'),
(17, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 15:08:19.512662', NULL),
(18, 2, 1, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 15:14:02.684267', NULL),
(19, 2, 1, 50, 10, 29, 'COMPLETED', '2026-08-06 15:14:02.684267', '2026-08-06 15:14:32.161730'),
(20, 2, 2, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 15:16:22.057384', NULL),
(21, 2, 2, NULL, 10, 10, 'INCOMPLETE', '2026-08-06 15:16:22.057384', '2026-08-06 15:16:32.751138');

-- ============================================================================
-- user_subscription
-- ============================================================================
DROP TABLE IF EXISTS `user_subscription`;
CREATE TABLE `user_subscription` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `plan_id` bigint NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `subscription_status` varchar(20) NOT NULL,
  `remaining_question_count` int NOT NULL,
  `version` bigint NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_subscription_plan` (`plan_id`),
  KEY `idx_user_subscription_user_plan_status` (`user_id`,`plan_id`,`subscription_status`),
  CONSTRAINT `fk_user_subscription_plan` FOREIGN KEY (`plan_id`) REFERENCES `subscription_plan` (`plan_id`),
  CONSTRAINT `fk_user_subscription_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 14 row(s)
INSERT INTO `user_subscription` (`id`, `user_id`, `plan_id`, `start_date`, `end_date`, `subscription_status`, `remaining_question_count`, `version`, `created_at`, `updated_at`) VALUES
(1, 1, 4, '2026-08-06', '2027-08-06', 'ACTIVE', 10000, 0, '2026-08-06 09:17:23.303672', '2026-08-06 09:17:23.303672'),
(2, 2, 1, '2026-08-06', '2028-08-06', 'CANCELLED', 50, 2, '2026-08-06 09:17:23.315802', '2026-08-06 10:26:21.623280'),
(3, 3, 1, '2026-08-06', '2027-08-06', 'ACTIVE', 50, 0, '2026-08-06 10:02:46.195842', '2026-08-06 10:02:46.195842'),
(4, 2, 2, '2026-08-06', '2027-08-06', 'CANCELLED', 500, 1, '2026-08-06 10:26:21.617756', '2026-08-06 10:26:34.542385'),
(5, 2, 1, '2026-08-06', '2027-08-06', 'CANCELLED', 50, 1, '2026-08-06 10:26:34.539858', '2026-08-06 10:27:01.181299'),
(6, 2, 2, '2026-08-06', '2027-08-06', 'CANCELLED', 500, 1, '2026-08-06 10:27:01.178195', '2026-08-06 11:22:56.560380'),
(7, 4, 1, '2026-08-06', '2027-08-06', 'ACTIVE', 50, 0, '2026-08-06 10:44:27.262914', '2026-08-06 10:44:27.262914'),
(8, 5, 1, '2026-08-06', '2027-08-06', 'ACTIVE', 50, 0, '2026-08-06 10:49:56.959555', '2026-08-06 10:49:56.959555'),
(9, 2, 3, '2026-08-06', '2027-08-06', 'CANCELLED', 2000, 1, '2026-08-06 11:22:56.555257', '2026-08-06 11:25:26.775490'),
(10, 2, 1, '2026-08-06', '2027-08-06', 'CANCELLED', 50, 1, '2026-08-06 11:25:26.771493', '2026-08-06 11:26:50.384580'),
(11, 2, 2, '2026-08-06', '2027-08-06', 'CANCELLED', 500, 1, '2026-08-06 11:26:50.382493', '2026-08-06 11:27:39.495813'),
(12, 2, 3, '2026-08-06', '2027-08-06', 'ACTIVE', 2000, 0, '2026-08-06 11:27:39.492774', '2026-08-06 11:27:39.492774'),
(13, 6, 1, '2026-08-06', '2027-08-06', 'ACTIVE', 50, 0, '2026-08-06 14:10:28.780192', '2026-08-06 14:10:28.780192'),
(14, 7, 1, '2026-08-06', '2027-08-06', 'ACTIVE', 50, 0, '2026-08-06 14:14:32.151270', '2026-08-06 14:14:32.151270');

-- ============================================================================
-- users
-- ============================================================================
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `mobile_number` varchar(20) DEFAULT NULL,
  `active` tinyint(1) NOT NULL,
  `role` varchar(50) NOT NULL,
  `created_at` timestamp(6) NOT NULL,
  `temporary_password` varchar(255) DEFAULT NULL,
  `temporary_password_expiry` timestamp(6) NULL DEFAULT NULL,
  `password_reset_required` tinyint(1) NOT NULL,
  `failed_login_attempts` int NOT NULL,
  `account_locked_until` timestamp(6) NULL DEFAULT NULL,
  `password_changed_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  KEY `idx_users_temporary_password_expiry` (`temporary_password_expiry`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 7 row(s)
INSERT INTO `users` (`id`, `name`, `email`, `password`, `mobile_number`, `active`, `role`, `created_at`, `temporary_password`, `temporary_password_expiry`, `password_reset_required`, `failed_login_attempts`, `account_locked_until`, `password_changed_at`) VALUES
(1, 'Admin', 'admin@interviewpilot.dev', '$2a$10$mzzn5z715F.GnjLJtgtue.hUQb8uaxHY8IEXRrZ7To2qJ1c9Vso5C', NULL, 1, 'ADMIN', '2026-08-06 09:17:23.146706', NULL, NULL, 0, 0, NULL, '2026-08-06 09:17:23.146706'),
(2, 'Demo Candidate', 'demo@interviewpilot.dev', '$2a$10$iPuni99aT0Yb2NxHp7nPLe7FBKkv6HDmIUefahlQl9SvYbhU.XFTu', NULL, 1, 'USER', '2026-08-06 09:17:23.274645', '$2a$10$YT0lvBFC9zR5W07XHYhvouelkOUgnB0CUa.1I3gDbyqaatZ8aTeTW', '2026-08-07 11:19:52.240300', 1, 0, NULL, '2026-08-06 11:02:02.782294'),
(3, 'Migration Test', 'migrationtest@example.com', '$2a$10$3w88mLfa8Tk7ecNvt9E/p.trkUWLPfRNr0v6dU1cCIlqNnubcJfk6', NULL, 1, 'USER', '2026-08-06 10:02:46.105298', NULL, NULL, 0, 0, NULL, '2026-08-06 10:02:46.105298'),
(4, 'FE Test', 'fetest1@example.com', '$2a$10$MmQHeZBkB3rqMF1uMkd9Mefr5Z0.VH.RXtmZTNHwuuFy12K72ARnO', '9999999999', 1, 'USER', '2026-08-06 10:44:27.247009', NULL, NULL, 0, 0, NULL, '2026-08-06 10:44:27.247009'),
(5, 'Test User', 'browsertest1@example.com', '$2a$10$WVXIcxgkBCA1OxYaY/wOM.IzLaG5.tFpCvd.25RLh4S1KVNXZcvua', '9999912345', 1, 'USER', '2026-08-06 10:49:56.947010', NULL, NULL, 0, 0, NULL, '2026-08-06 10:49:56.947010'),
(6, 'Arvind Kumar', 'arvind.kumar7@globallogic.com', '$2a$10$o7OVd/Srrl/DICjKYxeoMeJ5yxWCtjdGI3IAp7Z4eADw6m6Bllw2S', '09971366919', 1, 'USER', '2026-08-06 14:10:28.755460', NULL, NULL, 0, 0, NULL, '2026-08-06 14:10:28.755460'),
(7, 'Arvind Kumar', 'arvind.kumar8@globallogic.com', '$2a$10$eEcZDKpmDL12IprB9hVOjewCq.eWMI2GGsrhAszuRJJLkLRUvC7sq', '09971366919', 1, 'USER', '2026-08-06 14:14:32.129380', NULL, NULL, 0, 0, NULL, '2026-08-06 14:14:32.129380');

SET UNIQUE_CHECKS=1;
SET FOREIGN_KEY_CHECKS=1;
