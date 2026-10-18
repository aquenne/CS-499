# Morgan Lynch - Computer Science ePortfolio
**Bachelor of Science in Computer Science**

---

## Quick Navigation
* **Code Review Video:** [Watch Walkthrough on Google Drive](https://drive.google.com/)
* **Original Codebase (CS 360):** [Browse Original Code Folder](https://github.com/aquenne/CS-499/tree/main/original-code)
* **Enhanced Codebase (CS 499):** [Browse Enhanced Code Folder](https://github.com/aquenne/CS-499/tree/main/enhanced-code)
* **Documentation & Narratives:** [Browse Documentation Folder](https://github.com/aquenne/CS-499/tree/main/documentation)

---

## Table of Contents
1. [Professional Self-Assessment](#professional-self-assessment)
2. [Informal Code Review Video](#informal-code-review-video)
3. [Artifact 1: Software Design & Engineering](#artifact-1-software-design--engineering)
4. [Artifact 2: Algorithms & Data Structures](#artifact-2-algorithms--data-structures)
5. [Artifact 3: Databases & Security](#artifact-3-databases--security)
6. [Repository Structure](#repository-structure)

---

## Professional Self-Assessment

### Introduction & Career Goals
Throughout my Computer Science program at Southern New Hampshire University, I have focused on engineering secure, scalable, and high-performance software applications across mobile, web, and database systems. Building this professional ePortfolio has allowed me to consolidate my technical evolution into a cohesive showcase that reflects both my core engineering capabilities and my approach to modern system design. My professional standards are centered on technical precision, proactive defense-in-depth security, and building responsive user experiences that maximize hardware efficiency. As I transition into the professional software industry, my goal is to leverage my foundation in full-stack architecture to specialize in native Android development—applying Kotlin, Jetpack Compose, MVVM architecture, and Room ORM to build secure, enterprise-grade mobile applications that prioritize performance and data privacy.

### Core Competencies & Program Highlights

#### Collaborating in a Team Environment
Effective software development requires building collaborative, readable code environments that enable diverse technical and non-technical stakeholders to understand system logic and support informed decision-making. Throughout my coursework, I focused on writing clean, self-documenting Kotlin code paired with clear contextual in-code comments. This practice ensures that software modules remain transparent, maintainable, and easily digestible for project managers, future maintainers, and cross-functional teams. Furthermore, I actively incorporated iterative feedback from course instructors to refine software architecture, optimize database access patterns, and establish production-ready engineering standards.

#### Communicating with Stakeholders
Effective communication in software engineering requires translating complex technical implementations into clear, structured documentation. Throughout my capstone project, I focused on articulating architectural decisions, performance trade-offs, and security enhancements through detailed technical narratives and structured repository documentation. Whether detailing the memory efficiency of an O(1) Hash Map cache or justifying the migration to Room ORM and SHA-256 password salting, I prioritize presenting engineering choices clearly. Communicating the rationale behind software design choices ensures that codebases remain accessible, verifiable, and well-documented for evaluators, future developers, and project reviewers.

#### Data Structures and Algorithms
Engineers must evaluate computing solutions by balancing algorithmic performance against resource constraints. To optimize data processing in my capstone mobile application, InventoryManagerProLynch, I analyzed computational trade-offs and replaced inefficient O(N) linear lookups with an O(1) Hash Map scan caching mechanism (DataDecoder.kt). This optimization eliminated redundant database operations and drastically reduced lookup latency during high-frequency QR/barcode scanning. Additionally, I implemented CRC32 checksum calculations to verify payload integrity across asynchronous transfers, validating algorithmic correctness and edge-case execution through dedicated JUnit unit test suites.

#### Software Engineering & Database Architecture
My approach to software engineering relies on applying established Software Development Life Cycle (SDLC) principles, industry-standard design patterns, and iterative testing methodologies. To modernize legacy, monolithic codebases, I refactored procedural Android activity structures into a reactive Model-View-ViewModel (MVVM) pattern using Kotlin and Jetpack Compose. In the persistence layer, I migrated legacy, injection-prone SQLite code to the Room Object-Relational Mapping (ORM) framework. Utilizing structured Data Access Objects (DAOs) and compile-time query verification ensures data integrity while establishing a clean separation of concerns between database logic and user interface components.

#### Developing a Security Mindset
Adopting a proactive security mindset, by anticipating adversarial exploits during initial requirements and design phases, is essential for safeguarding modern software systems. In my capstone enhancements, I conducted threat modeling to identify vulnerabilities such as plain-text credential storage, unvalidated input streams, and unauthorized database access. To mitigate these risks, I built a cryptographic utility (SecurityUtils.kt) that replaces plain-text storage with SHA-256 password hashing combined with unique, cryptographically generated 16-byte random salts. Furthermore, I implemented strict Role-Based Access Control (RBAC) at the entity level to restrict destructive database actions to authenticated administrative accounts, ensuring complete data validation and privacy.

---

## Informal Code Review Video

The screencast walkthrough analyzes legacy code features, identifies target areas for structural and security improvements, and presents planned capstone enhancements.

**[Watch the Informal Code Review Video on Google Drive](https://drive.google.com/file/d/1g_RJIdc7K2GFqk2geg2kVA3SvjavFt_2/view?usp=sharing)**

---

## Artifact 1: Software Design & Engineering
* **Artifact Name:** Inventory Manager Pro (Mobile Android Application)
* **Focus Area:** Architectural Modernization, MVVM, Jetpack Compose & CameraX Hardware Integration
* **Key File Links:** 
  * [CameraScanScreen.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/main/java/com/example/inventorymanagerprolynch/CameraScanScreen.kt)
  * [InventoryViewModel.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/main/java/com/example/inventorymanagerprolynch/InventoryViewModel.kt)

### Narrative Summary
The artifact selected for this enhancement is my mobile application, Inventory Manager Pro, originally developed in CS 360. In its original state, the application relied on procedural activity code and static layout XML files. To modernize the software architecture, I refactored the application into a Model-View-ViewModel (MVVM) pattern using Kotlin and Jetpack Compose. I integrated a live CameraX preview pipeline for real-time QR and barcode capture, separating UI state handling from core business logic to ensure reactive UI updates and improved maintainability.

---

## Artifact 2: Algorithms & Data Structures
* **Artifact Name:** Inventory Manager Pro (Mobile Android Application)
* **Focus Area:** Algorithmic Optimization, O(1) Hash Map Scan Caching & CRC32 Checksum Verification
* **Key File Links:** 
  * [DataDecoder.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/main/java/com/example/inventorymanagerprolynch/DataDecoder.kt)
  * [DataDecoderTest.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/test/java/com/example/inventorymanagerprolynch/DataDecoderTest.kt)

### Narrative Summary
To address performance bottlenecks during frequent inventory lookups, I engineered an O(1) Hash Map caching mechanism (DataDecoder.kt) that intercepts scan payloads before executing database queries. Furthermore, I implemented CRC32 checksum calculation to verify data payload integrity across asynchronous transfers, validating algorithmic correctness and boundary performance using dedicated JUnit test suites.

---

## Artifact 3: Databases & Security
* **Artifact Name:** Inventory Manager Pro (Mobile Android Application)
* **Focus Area:** Local Persistence Migration (Room ORM), SHA-256 Salting/Hashing & Role-Based Access Control
* **Key File Links:** 
  * [InventoryDao.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/main/java/com/example/inventorymanagerprolynch/InventoryDao.kt)
  * [SecurityUtils.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/main/java/com/example/inventorymanagerprolynch/SecurityUtils.kt)
  * [UserEntity.kt](https://github.com/aquenne/CS-499/blob/main/enhanced-code/app/src/main/java/com/example/inventorymanagerprolynch/UserEntity.kt)

### Narrative Summary
In this enhancement, I replaced legacy SQLiteOpenHelper raw string queries with the Room Object-Relational Mapping (ORM) framework. This eliminates SQL injection attack vectors through parameterized Data Access Objects (InventoryDao.kt). To protect user credentials, I introduced a UserEntity table and developed SecurityUtils.kt to execute SHA-256 password hashing paired with cryptographically secure 16-byte random salts. Role-Based Access Control (RBAC) was also enforced across application sessions to restrict destructive database permissions to administrative accounts.

---

## Repository Structure

<pre><code>
CS-499/
├── README.md                            # Main ePortfolio Site Render
├── original-code/                       # Complete original CS 360 project
├── enhanced-code/                       # Complete enhanced CS 499 Capstone project
└── documentation/                       # Capstone Documents & Narratives (.docx)
    ├── Lynch_Professional_SelfAssessment.docx
    ├── Lynch_MilestoneTwo_Narrative.docx
    ├── Lynch_MilestoneThree_Narrative.docx
    ├── Lynch_MilestoneFour_Narrative.docx
    └── Lynch_CodeReview_Link.docx</code></pre>
