package com.malik.InterviewPilot.config;

import com.malik.InterviewPilot.entity.*;
import com.malik.InterviewPilot.razorpay.entity.PlanQuestion;
import com.malik.InterviewPilot.razorpay.entity.SubscriptionPlan;
import com.malik.InterviewPilot.razorpay.repository.PlanQuestionRepository;
import com.malik.InterviewPilot.razorpay.repository.SubscriptionPlanRepository;
import com.malik.InterviewPilot.razorpay.service.SubscriptionService;
import com.malik.InterviewPilot.repository.ExamRepository;
import com.malik.InterviewPilot.repository.QuestionRepository;
import com.malik.InterviewPilot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds demo accounts and a starter question bank so the app is usable immediately after
 * first boot. Runs after SubscriptionPlanSeeder (@Order(10)) so plan rows already exist
 * when these users are assigned one.
 */
@Component
@RequiredArgsConstructor
@Order(20)
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionService subscriptionService;
    private final PlanQuestionRepository planQuestionRepository;

    @Override
    public void run(String... args) {
        seedUsers();
        seedExamsAndQuestions();
        seedPlanQuestionAssignments();
    }

    private void seedUsers() {
        if (userRepository.existsByEmail("admin@interviewpilot.dev")) {
            return;
        }

        User admin = userRepository.save(User.builder()
                .name("Admin")
                .email("admin@interviewpilot.dev")
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.ADMIN)
                .build());

        User demo = userRepository.save(User.builder()
                .name("Demo Candidate")
                .email("demo@interviewpilot.dev")
                .password(passwordEncoder.encode("Demo@123"))
                .role(Role.USER)
                .build());

        subscriptionService.activateSubscription(admin, findPlanOrThrow("Enterprise"));
        subscriptionService.activateSubscription(demo, findPlanOrThrow("Free"));
    }

    private SubscriptionPlan findPlanOrThrow(String planName) {
        return subscriptionPlanRepository.findByPlanName(planName)
                .orElseThrow(() -> new IllegalStateException("Expected seeded plan not found: " + planName));
    }

    private void seedExamsAndQuestions() {
        if (examRepository.count() > 0) {
            return;
        }

        Exam java = examRepository.save(Exam.builder()
                .name("Core Java")
                .category("Java")
                .description("Java language fundamentals, OOP, and collections for interview prep.")
                .build());

        Exam springBoot = examRepository.save(Exam.builder()
                .name("Spring Boot")
                .category("Spring Boot")
                .description("Spring Boot, Spring MVC, Spring Data JPA, and Spring Security essentials.")
                .build());

        Exam aws = examRepository.save(Exam.builder()
                .name("AWS Cloud Practitioner")
                .category("AWS")
                .description("Core AWS services, pricing, and architecture for certification prep.")
                .build());

        Exam azure = examRepository.save(Exam.builder()
                .name("Azure Fundamentals")
                .category("Azure")
                .description("Core Azure services, governance, and pricing for AZ-900 style prep.")
                .build());

        questionRepository.saveAll(javaQuestions(java));
        questionRepository.saveAll(springBootQuestions(springBoot));
        questionRepository.saveAll(awsQuestions(aws));
        questionRepository.saveAll(azureQuestions(azure));
    }

    /**
     * Demonstrates cascading plan eligibility (a question available to Basic is also available
     * to every higher tier) by cycling questions through the four tiers exactly like the spec's
     * example: 1st question -> Free+Basic+Premium+Enterprise, 2nd -> Basic+Premium+Enterprise,
     * 3rd -> Premium+Enterprise, 4th -> Enterprise only, then the cycle repeats.
     */
    private void seedPlanQuestionAssignments() {
        if (planQuestionRepository.count() > 0) {
            return;
        }

        List<SubscriptionPlan> tiersLowToHigh = List.of(
                findPlanOrThrow("Free"), findPlanOrThrow("Basic"),
                findPlanOrThrow("Premium"), findPlanOrThrow("Enterprise"));

        List<Question> allQuestions = questionRepository.findAll();
        List<PlanQuestion> assignments = new ArrayList<>();
        for (int i = 0; i < allQuestions.size(); i++) {
            Question question = allQuestions.get(i);
            int minTier = i % tiersLowToHigh.size();
            for (int tier = minTier; tier < tiersLowToHigh.size(); tier++) {
                assignments.add(PlanQuestion.builder().plan(tiersLowToHigh.get(tier)).question(question).build());
            }
        }
        planQuestionRepository.saveAll(assignments);
    }

    private List<Question> javaQuestions(Exam exam) {
        return List.of(
                q(exam, "Which keyword is used to prevent a class from being subclassed in Java?",
                        "static", "final", "const", "sealed", "B",
                        "'final' on a class prevents any other class from extending it."),
                q(exam, "What is the default value of a boolean instance variable in Java?",
                        "true", "false", "null", "0", "B",
                        "Uninitialized boolean fields default to false."),
                q(exam, "Which collection type does NOT allow duplicate elements?",
                        "ArrayList", "LinkedList", "HashSet", "PriorityQueue", "C",
                        "Set implementations like HashSet enforce uniqueness of elements."),
                q(exam, "Which of these is a checked exception in Java?",
                        "NullPointerException", "ArithmeticException", "IOException", "ArrayIndexOutOfBoundsException", "C",
                        "IOException extends Exception directly and must be declared or caught; the others are unchecked RuntimeExceptions."),
                q(exam, "What does the 'volatile' keyword guarantee for a field?",
                        "Thread-safety for compound operations", "Visibility of writes across threads", "Atomicity of increment operations", "Immutability", "B",
                        "'volatile' ensures visibility — reads always see the latest write — but does not make compound operations atomic."),
                q(exam, "Which interface must a class implement to be used as a key in a HashMap reliably?",
                        "Comparable", "Serializable", "equals()/hashCode() contract", "Cloneable", "C",
                        "HashMap relies on a consistent equals()/hashCode() implementation to locate buckets and detect key equality."),
                q(exam, "What is the time complexity of retrieving an element from an ArrayList by index?",
                        "O(1)", "O(log n)", "O(n)", "O(n log n)", "A",
                        "ArrayList is backed by an array, so indexed access is constant time."),
                q(exam, "Which Java feature allows a lambda expression to access an enclosing local variable?",
                        "Any local variable", "Only static variables", "Effectively final local variables", "Only instance fields", "C",
                        "Lambdas can capture local variables only if they are final or effectively final."),
                q(exam, "What does the Stream API's 'reduce' operation do?",
                        "Filters elements matching a predicate", "Combines elements into a single result using an accumulator", "Sorts the stream", "Skips duplicate elements", "B",
                        "reduce() folds the stream elements into one result via a combining function."),
                q(exam, "Which of these correctly describes Java's garbage collection?",
                        "Manual memory deallocation via free()", "Automatic reclamation of unreachable objects", "Only runs when JVM exits", "Requires explicit destructor calls", "B",
                        "The JVM's garbage collector automatically reclaims memory for objects no longer reachable from GC roots."));
    }

    private List<Question> springBootQuestions(Exam exam) {
        return List.of(
                q(exam, "Which annotation marks a class as a Spring Boot application entry point?",
                        "@Component", "@SpringBootApplication", "@Configuration", "@Service", "B",
                        "@SpringBootApplication combines @Configuration, @EnableAutoConfiguration, and @ComponentScan."),
                q(exam, "What is the purpose of Spring Boot's auto-configuration?",
                        "To require XML config for every bean", "To automatically configure beans based on classpath contents and properties", "To disable all default beans", "To replace dependency injection", "B",
                        "Auto-configuration inspects the classpath and existing beans to wire up sensible defaults automatically."),
                q(exam, "Which annotation is used to inject a bean by type in Spring?",
                        "@Inject only", "@Autowired", "@Resource only", "@Bean", "B",
                        "@Autowired is Spring's primary annotation for dependency injection by type."),
                q(exam, "In Spring Data JPA, what does 'ddl-auto: update' do?",
                        "Drops and recreates the schema on every start", "Updates the schema to match entities without dropping existing data", "Disables schema generation", "Validates schema only", "B",
                        "'update' incrementally alters the schema to match entity mappings, preserving existing data."),
                q(exam, "Which Spring Security component authenticates incoming requests once per request in a filter chain?",
                        "DispatcherServlet", "OncePerRequestFilter", "ViewResolver", "HandlerMapping", "B",
                        "OncePerRequestFilter guarantees a single execution per request, commonly used for JWT authentication filters."),
                q(exam, "What HTTP status does @ResponseStatus(HttpStatus.CREATED) typically pair with?",
                        "GET requests fetching a resource", "POST requests that successfully create a resource", "DELETE requests", "PUT requests updating a resource", "B",
                        "201 Created is conventionally returned after a successful resource-creating POST."),
                q(exam, "What does @Transactional do when applied to a service method?",
                        "Runs the method in a separate thread", "Wraps the method in a database transaction, rolling back on runtime exceptions", "Caches the method result", "Logs method execution time", "B",
                        "@Transactional demarcates a transactional boundary and rolls back on unchecked exceptions by default."),
                q(exam, "Which starter would you add to expose actuator health/metrics endpoints?",
                        "spring-boot-starter-actuator", "spring-boot-starter-web", "spring-boot-starter-test", "spring-boot-starter-security", "A",
                        "spring-boot-starter-actuator adds production-ready monitoring endpoints like /actuator/health."),
                q(exam, "What is the primary benefit of constructor injection over field injection?",
                        "Faster runtime performance", "Enables immutable dependencies and easier testing", "Requires less code", "Works only with @Autowired", "B",
                        "Constructor injection allows final fields and makes required dependencies explicit and easily mockable in tests."),
                q(exam, "Which annotation restricts an endpoint to users with a specific role?",
                        "@RequestMapping", "@PreAuthorize(\"hasRole('ADMIN')\")", "@Valid", "@ResponseBody", "B",
                        "@PreAuthorize with a SpEL role expression enforces method-level role-based access control."));
    }

    private List<Question> awsQuestions(Exam exam) {
        return List.of(
                q(exam, "Which AWS service provides object storage?",
                        "EBS", "S3", "EFS", "RDS", "B",
                        "Amazon S3 is AWS's scalable object storage service."),
                q(exam, "What is the AWS shared responsibility model primarily about?",
                        "AWS handles all security", "Customer handles all security", "Security responsibilities split between AWS and the customer", "There is no shared responsibility in AWS", "C",
                        "AWS secures the cloud infrastructure; customers secure what they configure and store in the cloud."),
                q(exam, "Which service is used for serverless compute in AWS?",
                        "EC2", "Lambda", "Lightsail", "Elastic Beanstalk", "B",
                        "AWS Lambda runs code in response to events without provisioning or managing servers."),
                q(exam, "Which AWS service provides a managed relational database?",
                        "DynamoDB", "RDS", "S3", "SQS", "B",
                        "Amazon RDS manages relational databases like MySQL, PostgreSQL, and Aurora."),
                q(exam, "What does an IAM policy define?",
                        "Network routing rules", "Permissions for actions on AWS resources", "Billing thresholds", "EC2 instance types", "B",
                        "IAM policies are JSON documents that define allowed or denied actions on AWS resources."),
                q(exam, "Which AWS service is best suited for decoupling application components via message queues?",
                        "SNS", "SQS", "CloudFront", "Route 53", "B",
                        "Amazon SQS provides fully managed message queuing for decoupling microservices."),
                q(exam, "What is the purpose of an Availability Zone?",
                        "A billing region", "An isolated data center location within an AWS Region for fault tolerance", "A type of IAM role", "A CDN edge location", "B",
                        "Availability Zones are physically separate data centers within a Region, used for high availability."),
                q(exam, "Which pricing model offers the largest discount for steady-state, predictable workloads?",
                        "On-Demand", "Spot Instances", "Reserved Instances / Savings Plans", "Free Tier", "C",
                        "Reserved Instances and Savings Plans offer significant discounts in exchange for a usage commitment."),
                q(exam, "Which AWS service delivers content to users with low latency via edge locations?",
                        "CloudFront", "VPC", "IAM", "CloudTrail", "A",
                        "Amazon CloudFront is AWS's content delivery network (CDN)."),
                q(exam, "Which service records API calls made within an AWS account for auditing?",
                        "CloudWatch", "CloudTrail", "Config", "Inspector", "B",
                        "AWS CloudTrail logs API activity across an account for governance and auditing."));
    }

    private List<Question> azureQuestions(Exam exam) {
        return List.of(
                q(exam, "What is Azure Resource Manager (ARM) primarily used for?",
                        "Billing invoices only", "Deploying, managing, and organizing Azure resources as a group", "Monitoring network traffic", "User authentication only", "B",
                        "ARM is the deployment and management layer for creating, updating, and organizing Azure resources."),
                q(exam, "Which Azure service provides scalable object storage?",
                        "Azure Blob Storage", "Azure SQL Database", "Azure Functions", "Azure DevOps", "A",
                        "Azure Blob Storage is Microsoft's object storage solution for unstructured data."),
                q(exam, "What is the purpose of Azure Active Directory (Microsoft Entra ID)?",
                        "Object storage", "Identity and access management", "Virtual networking", "Container orchestration", "B",
                        "Azure AD (Microsoft Entra ID) is Azure's cloud-based identity and access management service."),
                q(exam, "Which Azure compute service is fully serverless (event-driven, no server management)?",
                        "Azure Virtual Machines", "Azure Functions", "Azure Kubernetes Service", "Azure Batch", "B",
                        "Azure Functions runs event-triggered code without requiring the user to manage servers."),
                q(exam, "What does an Azure Resource Group represent?",
                        "A billing currency", "A logical container for resources sharing the same lifecycle", "A network security rule", "A VM image", "B",
                        "Resource Groups group related resources for unified lifecycle management, access control, and billing."),
                q(exam, "Which Azure pricing tool estimates the cost of Azure products before deployment?",
                        "Azure Monitor", "Azure Pricing Calculator", "Azure Advisor", "Azure Policy", "B",
                        "The Azure Pricing Calculator estimates the cost of Azure services based on configuration."),
                q(exam, "Which service provides Infrastructure as a Service (IaaS) virtual machines in Azure?",
                        "Azure App Service", "Azure Virtual Machines", "Azure Logic Apps", "Azure Cognitive Services", "B",
                        "Azure Virtual Machines is Azure's core IaaS offering for provisioning VMs."),
                q(exam, "What is the main benefit of Azure Availability Zones?",
                        "Lower storage cost only", "Protection against datacenter-level failures within a region", "Faster DNS resolution", "Simplified billing", "B",
                        "Availability Zones are physically separate locations within an Azure region that protect against datacenter failures."),
                q(exam, "Which Azure governance feature enforces organizational rules on resource properties?",
                        "Azure Policy", "Azure Monitor", "Azure Backup", "Azure Bastion", "A",
                        "Azure Policy evaluates resources for compliance with defined rules and can enforce or audit them."),
                q(exam, "What is the Azure Service Level Agreement (SLA) used for?",
                        "Defining CPU architecture", "Microsoft's guaranteed uptime/performance commitment for a service", "Setting resource group names", "Configuring VNets", "B",
                        "An SLA defines Microsoft's guaranteed performance and uptime commitments for a given Azure service."));
    }

    private Question q(Exam exam, String question, String a, String b, String c, String d, String answer, String explanation) {
        return Question.builder()
                .exam(exam)
                .question(question)
                .optionA(a)
                .optionB(b)
                .optionC(c)
                .optionD(d)
                .answer(answer)
                .explanation(explanation)
                .difficulty("MEDIUM")
                .build();
    }
}
