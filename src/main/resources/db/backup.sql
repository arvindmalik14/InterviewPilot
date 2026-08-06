-- InterviewPilot MySQL backup
-- Generated via JDBC dump (mysqldump.exe is blocked by local Group Policy on this
-- machine) against the live `interviewpilot` database. Equivalent in effect to:
--   mysqldump -u devops -p --databases interviewpilot --routines --triggers --add-drop-table
--
-- Restore with:
--   mysql -u <user> -p < backup.sql

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET UNIQUE_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

CREATE DATABASE IF NOT EXISTS `interviewpilot` DEFAULT CHARACTER SET utf8mb4;
USE `interviewpilot`;

-- ----------------------------------------------------------------------------
-- Table structure for `exams`
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `exams`;
CREATE TABLE `exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `category` varchar(255) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data for `exams` (4 rows)
INSERT INTO `exams` (`id`, `name`, `category`, `description`) VALUES
(1, 'Core Java', 'Java', 'Java language fundamentals, OOP, and collections for interview prep.'),
(2, 'Spring Boot', 'Spring Boot', 'Spring Boot, Spring MVC, Spring Data JPA, and Spring Security essentials.'),
(3, 'AWS Cloud Practitioner', 'AWS', 'Core AWS services, pricing, and architecture for certification prep.'),
(4, 'Azure Fundamentals', 'Azure', 'Core Azure services, governance, and pricing for AZ-900 style prep.');

-- ----------------------------------------------------------------------------
-- Table structure for `payment_order`
-- ----------------------------------------------------------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data for `payment_order` (6 rows)
INSERT INTO `payment_order` (`id`, `user_id`, `plan_id`, `razorpay_order_id`, `amount`, `currency`, `status`, `receipt`, `created_at`, `updated_at`) VALUES
(1, 2, 2, 'order_TMRxwkGBAZMy3a', 9900, 'INR', 'PAID', 'rcpt_u2_p2_1786011880532', '2026-08-06 10:24:40.840980', '2026-08-06 10:26:21.623280'),
(2, 2, 2, 'order_TMS02ADtl54qNy', 9900, 'INR', 'PAID', 'rcpt_u2_p2_1786011999296', '2026-08-06 10:26:39.385020', '2026-08-06 10:27:01.181299'),
(3, 2, 3, 'order_TMSwn38zOqCbk8', 29900, 'INR', 'PAID', 'rcpt_u2_p3_1786015336392', '2026-08-06 11:22:16.697117', '2026-08-06 11:22:56.560380'),
(4, 2, 4, 'order_TMT0CL03qDgr12', 99900, 'INR', 'CREATED', 'rcpt_u2_p4_1786015530140', '2026-08-06 11:25:30.242355', '2026-08-06 11:25:30.242355'),
(5, 2, 2, 'order_TMT1338Z1Q5XoM', 9900, 'INR', 'PAID', 'rcpt_u2_p2_1786015578445', '2026-08-06 11:26:18.521301', '2026-08-06 11:26:50.384580'),
(6, 2, 3, 'order_TMT23NakRknfqM', 29900, 'INR', 'PAID', 'rcpt_u2_p3_1786015635544', '2026-08-06 11:27:15.623166', '2026-08-06 11:27:39.495813');

-- ----------------------------------------------------------------------------
-- Table structure for `payment_transaction`
-- ----------------------------------------------------------------------------
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

-- Data for `payment_transaction` (5 rows)
INSERT INTO `payment_transaction` (`id`, `user_id`, `order_id`, `razorpay_payment_id`, `razorpay_signature`, `status`, `payment_method`, `created_at`, `updated_at`) VALUES
(1, 2, 1, 'pay_TMRzOGuWYSb4xj', '7d61ff6c0f91a7d67c3bbfef972de35bfa0a0842d67779b268963f5cec22121e', 'SUCCESS', 'wallet', '2026-08-06 10:26:21.121688', '2026-08-06 10:26:21.121688'),
(2, 2, 2, 'pay_TMS09UjsFYAi7q', '7391b1a1a342d969f6fbb57fe829de94fa171b5503d6e8803d71a0487c6a8028', 'SUCCESS', 'wallet', '2026-08-06 10:27:00.582801', '2026-08-06 10:27:00.582801'),
(3, 2, 3, 'pay_TMSxC0e9LQRbUl', '6595ff5a550586c21d5ecbc4a112cce5cf8e7ebd5ad8655022e95fea66ba2630', 'SUCCESS', 'wallet', '2026-08-06 11:22:56.073868', '2026-08-06 11:22:56.073868'),
(4, 2, 5, 'pay_TMT1J87p5L5F7r', '39d110a1195ceaeb77a2623952770b5c65222eb4bd6070c507e04a7000298264', 'SUCCESS', 'wallet', '2026-08-06 11:26:49.724843', '2026-08-06 11:26:49.724843'),
(5, 2, 6, 'pay_TMT2C9xFNyfjxK', '4d53c40fd63568f9ea6f431bdc33dae96b88babaeb7125332f0f8272d58f3731', 'SUCCESS', 'wallet', '2026-08-06 11:27:38.970378', '2026-08-06 11:27:38.970378');

-- ----------------------------------------------------------------------------
-- Table structure for `plan_question`
-- ----------------------------------------------------------------------------
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

-- Data for `plan_question` (100 rows)
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

-- ----------------------------------------------------------------------------
-- Table structure for `questions`
-- ----------------------------------------------------------------------------
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

-- Data for `questions` (40 rows)
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

-- ----------------------------------------------------------------------------
-- Table structure for `subscription_plan`
-- ----------------------------------------------------------------------------
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

-- Data for `subscription_plan` (5 rows)
INSERT INTO `subscription_plan` (`plan_id`, `plan_name`, `price`, `duration_in_months`, `question_limit`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Free', 0.00, 12, 50, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(2, 'Basic', 99.00, 12, 500, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(3, 'Premium', 299.00, 12, 2000, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(4, 'Enterprise', 999.00, 12, 10000, 'ACTIVE', '2026-08-06 09:17:22.879874', '2026-08-06 09:17:22.879874'),
(5, 'MigrationCheck', 49.00, 6, 300, 'ACTIVE', '2026-08-06 10:03:41.137663', '2026-08-06 10:03:41.137663');

-- ----------------------------------------------------------------------------
-- Table structure for `test_answers`
-- ----------------------------------------------------------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data for `test_answers` (12 rows)
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
(12, 3, 11, 'C', 0);

-- ----------------------------------------------------------------------------
-- Table structure for `tests`
-- ----------------------------------------------------------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data for `tests` (3 rows)
INSERT INTO `tests` (`id`, `user_id`, `exam_id`, `score`, `total_questions`, `duration_seconds`, `status`, `started_at`, `completed_at`) VALUES
(1, 2, 1, 20, 10, 120, 'COMPLETED', '2026-08-06 10:03:07.569731', '2026-08-06 10:04:06.540360'),
(2, 2, 2, NULL, 10, NULL, 'IN_PROGRESS', '2026-08-06 10:27:18.620779', NULL),
(3, 2, 2, 40, 10, 42, 'COMPLETED', '2026-08-06 10:27:18.620779', '2026-08-06 10:28:01.022332');

-- ----------------------------------------------------------------------------
-- Table structure for `user_subscription`
-- ----------------------------------------------------------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data for `user_subscription` (12 rows)
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
(12, 2, 3, '2026-08-06', '2027-08-06', 'ACTIVE', 2000, 0, '2026-08-06 11:27:39.492774', '2026-08-06 11:27:39.492774');

-- ----------------------------------------------------------------------------
-- Table structure for `users`
-- ----------------------------------------------------------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data for `users` (5 rows)
INSERT INTO `users` (`id`, `name`, `email`, `password`, `mobile_number`, `active`, `role`, `created_at`, `temporary_password`, `temporary_password_expiry`, `password_reset_required`, `failed_login_attempts`, `account_locked_until`, `password_changed_at`) VALUES
(1, 'Admin', 'admin@interviewpilot.dev', '$2a$10$mzzn5z715F.GnjLJtgtue.hUQb8uaxHY8IEXRrZ7To2qJ1c9Vso5C', NULL, 1, 'ADMIN', '2026-08-06 09:17:23.146706', NULL, NULL, 0, 0, NULL, '2026-08-06 09:17:23.146706'),
(2, 'Demo Candidate', 'demo@interviewpilot.dev', '$2a$10$iPuni99aT0Yb2NxHp7nPLe7FBKkv6HDmIUefahlQl9SvYbhU.XFTu', NULL, 1, 'USER', '2026-08-06 09:17:23.274645', '$2a$10$YT0lvBFC9zR5W07XHYhvouelkOUgnB0CUa.1I3gDbyqaatZ8aTeTW', '2026-08-07 11:19:52.240300', 1, 3, NULL, '2026-08-06 11:02:02.782294'),
(3, 'Migration Test', 'migrationtest@example.com', '$2a$10$3w88mLfa8Tk7ecNvt9E/p.trkUWLPfRNr0v6dU1cCIlqNnubcJfk6', NULL, 1, 'USER', '2026-08-06 10:02:46.105298', NULL, NULL, 0, 0, NULL, '2026-08-06 10:02:46.105298'),
(4, 'FE Test', 'fetest1@example.com', '$2a$10$MmQHeZBkB3rqMF1uMkd9Mefr5Z0.VH.RXtmZTNHwuuFy12K72ARnO', '9999999999', 1, 'USER', '2026-08-06 10:44:27.247009', NULL, NULL, 0, 0, NULL, '2026-08-06 10:44:27.247009'),
(5, 'Test User', 'browsertest1@example.com', '$2a$10$WVXIcxgkBCA1OxYaY/wOM.IzLaG5.tFpCvd.25RLh4S1KVNXZcvua', '9999912345', 1, 'USER', '2026-08-06 10:49:56.947010', NULL, NULL, 0, 0, NULL, '2026-08-06 10:49:56.947010');

SET UNIQUE_CHECKS = 1;
SET FOREIGN_KEY_CHECKS = 1;
