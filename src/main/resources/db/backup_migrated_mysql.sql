-- InterviewPilot — H2 → MySQL data migration script.
--
-- Converts and loads the data captured in `backup.sql` (an H2 SCRIPT export) into MySQL.
-- Run this ONCE against a fresh `interviewpilot` database, before starting the app with
-- ddl-auto:update — Hibernate will then just reconcile (add anything missing) rather than
-- create from scratch, and your real data survives the migration.
--
-- This is NOT idempotent (unlike InterviewPilot_schema.sql's plan-catalog seed) — it inserts
-- fixed primary-key values captured from the H2 export. Run it exactly once against an empty
-- schema; running it twice will fail on duplicate-key/unique-constraint violations by design
-- (that's the safety net against accidentally double-loading the same data).
--
-- H2 → MySQL conversions applied here (see MIGRATION.md for the full list and reasoning):
--   * CREATE MEMORY TABLE ...                    → not needed at all: schema comes from
--                                                   InterviewPilot_schema.sql, run first.
--   * "PUBLIC"."TABLE"/"COLUMN" (double-quoted,   → backtick-quoted, unqualified (the target
--     schema-qualified, upper-case identifiers)     database IS the schema in MySQL).
--   * TIMESTAMP WITH TIME ZONE 'x+00'             → plain 'x' string literal (MySQL TIMESTAMP
--                                                   has no per-value zone; the source app
--                                                   itself always writes/reads UTC instants).
--   * DATE 'x'                                    → plain 'x' string literal.
--   * TRUE / FALSE                                → 1 / 0 (matches the TINYINT(1) columns).
--   * U&'...\2014...' (H2 Unicode-escape string)  → the actual UTF-8 em dash character,
--                                                   MySQL has no U&'...' literal syntax.
--   * IDENTITY(... RESTART WITH N ...)            → handled via the ALTER TABLE ... AUTO_INCREMENT
--                                                   statements at the end of this file, since
--                                                   explicit PK values are inserted here directly.
--
-- Run:
--   mysql -u <user> -p interviewpilot < InterviewPilot_schema.sql
--   mysql -u <user> -p interviewpilot < backup_migrated_mysql.sql

USE `interviewpilot`;

-- ============================================================================
-- exams (4 rows)
-- ============================================================================
INSERT INTO `exams` (`id`, `category`, `description`, `name`) VALUES
(1, 'Java', 'Java language fundamentals, OOP, and collections for interview prep.', 'Core Java'),
(2, 'Spring Boot', 'Spring Boot, Spring MVC, Spring Data JPA, and Spring Security essentials.', 'Spring Boot'),
(3, 'AWS', 'Core AWS services, pricing, and architecture for certification prep.', 'AWS Cloud Practitioner'),
(4, 'Azure', 'Core Azure services, governance, and pricing for AZ-900 style prep.', 'Azure Fundamentals');

-- ============================================================================
-- questions (40 rows)
-- ============================================================================
INSERT INTO `questions` (`id`, `answer`, `difficulty`, `explanation`, `optiona`, `optionb`, `optionc`, `optiond`, `question`, `exam_id`) VALUES
(1, 'B', 'MEDIUM', '''final'' on a class prevents any other class from extending it.', 'static', 'final', 'const', 'sealed', 'Which keyword is used to prevent a class from being subclassed in Java?', 1),
(2, 'B', 'MEDIUM', 'Uninitialized boolean fields default to false.', 'true', 'false', 'null', '0', 'What is the default value of a boolean instance variable in Java?', 1),
(3, 'C', 'MEDIUM', 'Set implementations like HashSet enforce uniqueness of elements.', 'ArrayList', 'LinkedList', 'HashSet', 'PriorityQueue', 'Which collection type does NOT allow duplicate elements?', 1),
(4, 'C', 'MEDIUM', 'IOException extends Exception directly and must be declared or caught; the others are unchecked RuntimeExceptions.', 'NullPointerException', 'ArithmeticException', 'IOException', 'ArrayIndexOutOfBoundsException', 'Which of these is a checked exception in Java?', 1),
(5, 'B', 'MEDIUM', '''volatile'' ensures visibility — reads always see the latest write — but does not make compound operations atomic.', 'Thread-safety for compound operations', 'Visibility of writes across threads', 'Atomicity of increment operations', 'Immutability', 'What does the ''volatile'' keyword guarantee for a field?', 1),
(6, 'C', 'MEDIUM', 'HashMap relies on a consistent equals()/hashCode() implementation to locate buckets and detect key equality.', 'Comparable', 'Serializable', 'equals()/hashCode() contract', 'Cloneable', 'Which interface must a class implement to be used as a key in a HashMap reliably?', 1),
(7, 'A', 'MEDIUM', 'ArrayList is backed by an array, so indexed access is constant time.', 'O(1)', 'O(log n)', 'O(n)', 'O(n log n)', 'What is the time complexity of retrieving an element from an ArrayList by index?', 1),
(8, 'C', 'MEDIUM', 'Lambdas can capture local variables only if they are final or effectively final.', 'Any local variable', 'Only static variables', 'Effectively final local variables', 'Only instance fields', 'Which Java feature allows a lambda expression to access an enclosing local variable?', 1),
(9, 'B', 'MEDIUM', 'reduce() folds the stream elements into one result via a combining function.', 'Filters elements matching a predicate', 'Combines elements into a single result using an accumulator', 'Sorts the stream', 'Skips duplicate elements', 'What does the Stream API''s ''reduce'' operation do?', 1),
(10, 'B', 'MEDIUM', 'The JVM''s garbage collector automatically reclaims memory for objects no longer reachable from GC roots.', 'Manual memory deallocation via free()', 'Automatic reclamation of unreachable objects', 'Only runs when JVM exits', 'Requires explicit destructor calls', 'Which of these correctly describes Java''s garbage collection?', 1),
(11, 'B', 'MEDIUM', '@SpringBootApplication combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.', '@Component', '@SpringBootApplication', '@Configuration', '@Service', 'Which annotation marks a class as a Spring Boot application entry point?', 2),
(12, 'B', 'MEDIUM', 'Auto-configuration inspects the classpath and existing beans to wire up sensible defaults automatically.', 'To require XML config for every bean', 'To automatically configure beans based on classpath contents and properties', 'To disable all default beans', 'To replace dependency injection', 'What is the purpose of Spring Boot''s auto-configuration?', 2),
(13, 'B', 'MEDIUM', '@Autowired is Spring''s primary annotation for dependency injection by type.', '@Inject only', '@Autowired', '@Resource only', '@Bean', 'Which annotation is used to inject a bean by type in Spring?', 2),
(14, 'B', 'MEDIUM', '''update'' incrementally alters the schema to match entity mappings, preserving existing data.', 'Drops and recreates the schema on every start', 'Updates the schema to match entities without dropping existing data', 'Disables schema generation', 'Validates schema only', 'In Spring Data JPA, what does ''ddl-auto: update'' do?', 2),
(15, 'B', 'MEDIUM', 'OncePerRequestFilter guarantees a single execution per request, commonly used for JWT authentication filters.', 'DispatcherServlet', 'OncePerRequestFilter', 'ViewResolver', 'HandlerMapping', 'Which Spring Security component authenticates incoming requests once per request in a filter chain?', 2),
(16, 'B', 'MEDIUM', '201 Created is conventionally returned after a successful resource-creating POST.', 'GET requests fetching a resource', 'POST requests that successfully create a resource', 'DELETE requests', 'PUT requests updating a resource', 'What HTTP status does @ResponseStatus(HttpStatus.CREATED) typically pair with?', 2),
(17, 'B', 'MEDIUM', '@Transactional demarcates a transactional boundary and rolls back on unchecked exceptions by default.', 'Runs the method in a separate thread', 'Wraps the method in a database transaction, rolling back on runtime exceptions', 'Caches the method result', 'Logs method execution time', 'What does @Transactional do when applied to a service method?', 2),
(18, 'A', 'MEDIUM', 'spring-boot-starter-actuator adds production-ready monitoring endpoints like /actuator/health.', 'spring-boot-starter-actuator', 'spring-boot-starter-web', 'spring-boot-starter-test', 'spring-boot-starter-security', 'Which starter would you add to expose actuator health/metrics endpoints?', 2),
(19, 'B', 'MEDIUM', 'Constructor injection allows final fields and makes required dependencies explicit and easily mockable in tests.', 'Faster runtime performance', 'Enables immutable dependencies and easier testing', 'Requires less code', 'Works only with @Autowired', 'What is the primary benefit of constructor injection over field injection?', 2),
(20, 'B', 'MEDIUM', '@PreAuthorize with a SpEL role expression enforces method-level role-based access control.', '@RequestMapping', '@PreAuthorize("hasRole(''ADMIN'')")', '@Valid', '@ResponseBody', 'Which annotation restricts an endpoint to users with a specific role?', 2),
(21, 'B', 'MEDIUM', 'Amazon S3 is AWS''s scalable object storage service.', 'EBS', 'S3', 'EFS', 'RDS', 'Which AWS service provides object storage?', 3),
(22, 'C', 'MEDIUM', 'AWS secures the cloud infrastructure; customers secure what they configure and store in the cloud.', 'AWS handles all security', 'Customer handles all security', 'Security responsibilities split between AWS and the customer', 'There is no shared responsibility in AWS', 'What is the AWS shared responsibility model primarily about?', 3),
(23, 'B', 'MEDIUM', 'AWS Lambda runs code in response to events without provisioning or managing servers.', 'EC2', 'Lambda', 'Lightsail', 'Elastic Beanstalk', 'Which service is used for serverless compute in AWS?', 3),
(24, 'B', 'MEDIUM', 'Amazon RDS manages relational databases like MySQL, PostgreSQL, and Aurora.', 'DynamoDB', 'RDS', 'S3', 'SQS', 'Which AWS service provides a managed relational database?', 3),
(25, 'B', 'MEDIUM', 'IAM policies are JSON documents that define allowed or denied actions on AWS resources.', 'Network routing rules', 'Permissions for actions on AWS resources', 'Billing thresholds', 'EC2 instance types', 'What does an IAM policy define?', 3),
(26, 'B', 'MEDIUM', 'Amazon SQS provides fully managed message queuing for decoupling microservices.', 'SNS', 'SQS', 'CloudFront', 'Route 53', 'Which AWS service is best suited for decoupling application components via message queues?', 3),
(27, 'B', 'MEDIUM', 'Availability Zones are physically separate data centers within a Region, used for high availability.', 'A billing region', 'An isolated data center location within an AWS Region for fault tolerance', 'A type of IAM role', 'A CDN edge location', 'What is the purpose of an Availability Zone?', 3),
(28, 'C', 'MEDIUM', 'Reserved Instances and Savings Plans offer significant discounts in exchange for a usage commitment.', 'On-Demand', 'Spot Instances', 'Reserved Instances / Savings Plans', 'Free Tier', 'Which pricing model offers the largest discount for steady-state, predictable workloads?', 3),
(29, 'A', 'MEDIUM', 'Amazon CloudFront is AWS''s content delivery network (CDN).', 'CloudFront', 'VPC', 'IAM', 'CloudTrail', 'Which AWS service delivers content to users with low latency via edge locations?', 3),
(30, 'B', 'MEDIUM', 'AWS CloudTrail logs API activity across an account for governance and auditing.', 'CloudWatch', 'CloudTrail', 'Config', 'Inspector', 'Which service records API calls made within an AWS account for auditing?', 3),
(31, 'B', 'MEDIUM', 'ARM is the deployment and management layer for creating, updating, and organizing Azure resources.', 'Billing invoices only', 'Deploying, managing, and organizing Azure resources as a group', 'Monitoring network traffic', 'User authentication only', 'What is Azure Resource Manager (ARM) primarily used for?', 4),
(32, 'A', 'MEDIUM', 'Azure Blob Storage is Microsoft''s object storage solution for unstructured data.', 'Azure Blob Storage', 'Azure SQL Database', 'Azure Functions', 'Azure DevOps', 'Which Azure service provides scalable object storage?', 4),
(33, 'B', 'MEDIUM', 'Azure AD (Microsoft Entra ID) is Azure''s cloud-based identity and access management service.', 'Object storage', 'Identity and access management', 'Virtual networking', 'Container orchestration', 'What is the purpose of Azure Active Directory (Microsoft Entra ID)?', 4),
(34, 'B', 'MEDIUM', 'Azure Functions runs event-triggered code without requiring the user to manage servers.', 'Azure Virtual Machines', 'Azure Functions', 'Azure Kubernetes Service', 'Azure Batch', 'Which Azure compute service is fully serverless (event-driven, no server management)?', 4),
(35, 'B', 'MEDIUM', 'Resource Groups group related resources for unified lifecycle management, access control, and billing.', 'A billing currency', 'A logical container for resources sharing the same lifecycle', 'A network security rule', 'A VM image', 'What does an Azure Resource Group represent?', 4),
(36, 'B', 'MEDIUM', 'The Azure Pricing Calculator estimates the cost of Azure services based on configuration.', 'Azure Monitor', 'Azure Pricing Calculator', 'Azure Advisor', 'Azure Policy', 'Which Azure pricing tool estimates the cost of Azure products before deployment?', 4),
(37, 'B', 'MEDIUM', 'Azure Virtual Machines is Azure''s core IaaS offering for provisioning VMs.', 'Azure App Service', 'Azure Virtual Machines', 'Azure Logic Apps', 'Azure Cognitive Services', 'Which service provides Infrastructure as a Service (IaaS) virtual machines in Azure?', 4),
(38, 'B', 'MEDIUM', 'Availability Zones are physically separate locations within an Azure region that protect against datacenter failures.', 'Lower storage cost only', 'Protection against datacenter-level failures within a region', 'Faster DNS resolution', 'Simplified billing', 'What is the main benefit of Azure Availability Zones?', 4),
(39, 'A', 'MEDIUM', 'Azure Policy evaluates resources for compliance with defined rules and can enforce or audit them.', 'Azure Policy', 'Azure Monitor', 'Azure Backup', 'Azure Bastion', 'Which Azure governance feature enforces organizational rules on resource properties?', 4),
(40, 'B', 'MEDIUM', 'An SLA defines Microsoft''s guaranteed performance and uptime commitments for a given Azure service.', 'Defining CPU architecture', 'Microsoft''s guaranteed uptime/performance commitment for a service', 'Setting resource group names', 'Configuring VNets', 'What is the Azure Service Level Agreement (SLA) used for?', 4);

-- ============================================================================
-- subscription_plan (4 rows) — real captured data supersedes InterviewPilot_schema.sql's
-- idempotent seed INSERTs for these same four rows; run this file after that one.
-- ============================================================================
INSERT INTO `subscription_plan` (`plan_id`, `created_at`, `duration_in_months`, `plan_name`, `price`, `question_limit`, `status`, `updated_at`) VALUES
(1, '2026-08-06 09:17:22.879874', 12, 'Free', 0.00, 50, 'ACTIVE', '2026-08-06 09:17:22.879874'),
(2, '2026-08-06 09:17:22.879874', 12, 'Basic', 99.00, 500, 'ACTIVE', '2026-08-06 09:17:22.879874'),
(3, '2026-08-06 09:17:22.879874', 12, 'Premium', 299.00, 2000, 'ACTIVE', '2026-08-06 09:17:22.879874'),
(4, '2026-08-06 09:17:22.879874', 12, 'Enterprise', 999.00, 10000, 'ACTIVE', '2026-08-06 09:17:22.879874')
-- If InterviewPilot_schema.sql's own plan-catalog seed already ran (it inserts the same 4
-- plans via auto-increment when run against an empty table), this overwrites those
-- placeholder rows with the real captured timestamps/data instead of erroring on the
-- duplicate plan_id — correct regardless of which of the two scripts happened to run first.
ON DUPLICATE KEY UPDATE
    `created_at` = VALUES(`created_at`),
    `duration_in_months` = VALUES(`duration_in_months`),
    `plan_name` = VALUES(`plan_name`),
    `price` = VALUES(`price`),
    `question_limit` = VALUES(`question_limit`),
    `status` = VALUES(`status`),
    `updated_at` = VALUES(`updated_at`);

-- ============================================================================
-- plan_question (100 rows)
-- ============================================================================
INSERT INTO `plan_question` (`id`, `created_at`, `plan_id`, `question_id`) VALUES
(1, '2026-08-06 09:17:23.408572', 1, 1),
(2, '2026-08-06 09:17:23.408572', 2, 1),
(3, '2026-08-06 09:17:23.408572', 3, 1),
(4, '2026-08-06 09:17:23.408572', 4, 1),
(5, '2026-08-06 09:17:23.408572', 2, 2),
(6, '2026-08-06 09:17:23.408572', 3, 2),
(7, '2026-08-06 09:17:23.408572', 4, 2),
(8, '2026-08-06 09:17:23.408572', 3, 3),
(9, '2026-08-06 09:17:23.408572', 4, 3),
(10, '2026-08-06 09:17:23.408572', 4, 4),
(11, '2026-08-06 09:17:23.408572', 1, 5),
(12, '2026-08-06 09:17:23.408572', 2, 5),
(13, '2026-08-06 09:17:23.408572', 3, 5),
(14, '2026-08-06 09:17:23.408572', 4, 5),
(15, '2026-08-06 09:17:23.408572', 2, 6),
(16, '2026-08-06 09:17:23.408572', 3, 6),
(17, '2026-08-06 09:17:23.408572', 4, 6),
(18, '2026-08-06 09:17:23.408572', 3, 7),
(19, '2026-08-06 09:17:23.408572', 4, 7),
(20, '2026-08-06 09:17:23.408572', 4, 8),
(21, '2026-08-06 09:17:23.408572', 1, 9),
(22, '2026-08-06 09:17:23.408572', 2, 9),
(23, '2026-08-06 09:17:23.408572', 3, 9),
(24, '2026-08-06 09:17:23.408572', 4, 9),
(25, '2026-08-06 09:17:23.408572', 2, 10),
(26, '2026-08-06 09:17:23.408572', 3, 10),
(27, '2026-08-06 09:17:23.408572', 4, 10),
(28, '2026-08-06 09:17:23.408572', 3, 11),
(29, '2026-08-06 09:17:23.408572', 4, 11),
(30, '2026-08-06 09:17:23.408572', 4, 12),
(31, '2026-08-06 09:17:23.408572', 1, 13),
(32, '2026-08-06 09:17:23.408572', 2, 13),
(33, '2026-08-06 09:17:23.408572', 3, 13),
(34, '2026-08-06 09:17:23.408572', 4, 13),
(35, '2026-08-06 09:17:23.408572', 2, 14),
(36, '2026-08-06 09:17:23.408572', 3, 14),
(37, '2026-08-06 09:17:23.408572', 4, 14),
(38, '2026-08-06 09:17:23.408572', 3, 15),
(39, '2026-08-06 09:17:23.408572', 4, 15),
(40, '2026-08-06 09:17:23.408572', 4, 16),
(41, '2026-08-06 09:17:23.408572', 1, 17),
(42, '2026-08-06 09:17:23.408572', 2, 17),
(43, '2026-08-06 09:17:23.408572', 3, 17),
(44, '2026-08-06 09:17:23.408572', 4, 17),
(45, '2026-08-06 09:17:23.408572', 2, 18),
(46, '2026-08-06 09:17:23.408572', 3, 18),
(47, '2026-08-06 09:17:23.408572', 4, 18),
(48, '2026-08-06 09:17:23.408572', 3, 19),
(49, '2026-08-06 09:17:23.408572', 4, 19),
(50, '2026-08-06 09:17:23.408572', 4, 20),
(51, '2026-08-06 09:17:23.408572', 1, 21),
(52, '2026-08-06 09:17:23.408572', 2, 21),
(53, '2026-08-06 09:17:23.408572', 3, 21),
(54, '2026-08-06 09:17:23.408572', 4, 21),
(55, '2026-08-06 09:17:23.408572', 2, 22),
(56, '2026-08-06 09:17:23.408572', 3, 22),
(57, '2026-08-06 09:17:23.408572', 4, 22),
(58, '2026-08-06 09:17:23.408572', 3, 23),
(59, '2026-08-06 09:17:23.408572', 4, 23),
(60, '2026-08-06 09:17:23.408572', 4, 24),
(61, '2026-08-06 09:17:23.408572', 1, 25),
(62, '2026-08-06 09:17:23.408572', 2, 25),
(63, '2026-08-06 09:17:23.408572', 3, 25),
(64, '2026-08-06 09:17:23.408572', 4, 25),
(65, '2026-08-06 09:17:23.408572', 2, 26),
(66, '2026-08-06 09:17:23.408572', 3, 26),
(67, '2026-08-06 09:17:23.408572', 4, 26),
(68, '2026-08-06 09:17:23.408572', 3, 27),
(69, '2026-08-06 09:17:23.408572', 4, 27),
(70, '2026-08-06 09:17:23.408572', 4, 28),
(71, '2026-08-06 09:17:23.408572', 1, 29),
(72, '2026-08-06 09:17:23.408572', 2, 29),
(73, '2026-08-06 09:17:23.408572', 3, 29),
(74, '2026-08-06 09:17:23.408572', 4, 29),
(75, '2026-08-06 09:17:23.408572', 2, 30),
(76, '2026-08-06 09:17:23.408572', 3, 30),
(77, '2026-08-06 09:17:23.408572', 4, 30),
(78, '2026-08-06 09:17:23.408572', 3, 31),
(79, '2026-08-06 09:17:23.408572', 4, 31),
(80, '2026-08-06 09:17:23.408572', 4, 32),
(81, '2026-08-06 09:17:23.408572', 1, 33),
(82, '2026-08-06 09:17:23.408572', 2, 33),
(83, '2026-08-06 09:17:23.408572', 3, 33),
(84, '2026-08-06 09:17:23.408572', 4, 33),
(85, '2026-08-06 09:17:23.408572', 2, 34),
(86, '2026-08-06 09:17:23.408572', 3, 34),
(87, '2026-08-06 09:17:23.408572', 4, 34),
(88, '2026-08-06 09:17:23.408572', 3, 35),
(89, '2026-08-06 09:17:23.408572', 4, 35),
(90, '2026-08-06 09:17:23.408572', 4, 36),
(91, '2026-08-06 09:17:23.408572', 1, 37),
(92, '2026-08-06 09:17:23.408572', 2, 37),
(93, '2026-08-06 09:17:23.408572', 3, 37),
(94, '2026-08-06 09:17:23.408572', 4, 37),
(95, '2026-08-06 09:17:23.408572', 2, 38),
(96, '2026-08-06 09:17:23.408572', 3, 38),
(97, '2026-08-06 09:17:23.408572', 4, 38),
(98, '2026-08-06 09:17:23.408572', 3, 39),
(99, '2026-08-06 09:17:23.408572', 4, 39),
(100, '2026-08-06 09:17:23.408572', 4, 40);

-- ============================================================================
-- users (2 rows)
-- ============================================================================
INSERT INTO `users` (`id`, `account_locked_until`, `active`, `created_at`, `email`, `failed_login_attempts`, `mobile_number`, `name`, `password`, `password_changed_at`, `password_reset_required`, `role`, `temporary_password_expiry`, `temporary_password`) VALUES
(1, NULL, 1, '2026-08-06 09:17:23.146706', 'admin@interviewpilot.dev', 0, NULL, 'Admin', '$2a$10$mzzn5z715F.GnjLJtgtue.hUQb8uaxHY8IEXRrZ7To2qJ1c9Vso5C', '2026-08-06 09:17:23.146706', 0, 'ADMIN', NULL, NULL),
(2, NULL, 1, '2026-08-06 09:17:23.274645', 'demo@interviewpilot.dev', 0, NULL, 'Demo Candidate', '$2a$10$Nb8DuyLL0KCaz1sOwfeXWOcwmhs5urA0pxjm9t3RLEPkWUWv3Bf/y', '2026-08-06 09:17:23.274645', 0, 'USER', NULL, NULL);

-- ============================================================================
-- user_subscription (2 rows)
-- ============================================================================
INSERT INTO `user_subscription` (`id`, `created_at`, `end_date`, `remaining_question_count`, `start_date`, `subscription_status`, `updated_at`, `version`, `plan_id`, `user_id`) VALUES
(1, '2026-08-06 09:17:23.303672', '2027-08-06', 10000, '2026-08-06', 'ACTIVE', '2026-08-06 09:17:23.303672', 0, 4, 1),
(2, '2026-08-06 09:17:23.315802', '2027-08-06', 50, '2026-08-06', 'ACTIVE', '2026-08-06 09:17:23.315802', 0, 1, 2);

-- payment_order, payment_transaction, tests, test_answers: 0 rows in the source export — nothing to load.

-- ============================================================================
-- Re-point AUTO_INCREMENT past the highest imported id, matching the source H2 export's
-- "RESTART WITH" counters — otherwise the next INSERT from the app would collide with an
-- already-imported primary key.
-- ============================================================================
ALTER TABLE `exams` AUTO_INCREMENT = 5;
ALTER TABLE `questions` AUTO_INCREMENT = 41;
ALTER TABLE `subscription_plan` AUTO_INCREMENT = 5;
ALTER TABLE `plan_question` AUTO_INCREMENT = 101;
ALTER TABLE `users` AUTO_INCREMENT = 3;
ALTER TABLE `user_subscription` AUTO_INCREMENT = 3;
ALTER TABLE `payment_order` AUTO_INCREMENT = 1;
ALTER TABLE `payment_transaction` AUTO_INCREMENT = 1;
ALTER TABLE `tests` AUTO_INCREMENT = 1;
ALTER TABLE `test_answers` AUTO_INCREMENT = 1;
