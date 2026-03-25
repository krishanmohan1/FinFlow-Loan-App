APPLICATION SERVICE — PROBLEM ANALYSIS & IMPROVEMENTS

---

##  PROBLEMS IDENTIFIED

1.  Admin Endpoints Not Secured

* Update status and delete APIs had NO role validation.
* Any user (or attacker via gateway bypass) could call them.
* This is a critical security issue.

2.  User Could Not Filter Loans by Status

* Endpoint `/status/{status}` was restricted only to ADMIN.
* Requirement was: USER should also see their own loans by status.

3.  Confusing Username Endpoint

* `/user/{username}` allowed USER access but ignored path variable.
* This created confusion and poor API design.
* Users should NOT fetch loans by username.

4.  Partial Security Only in Controller

* Security logic was only in controller.
* Service layer had no protection.
* Risk: If controller is bypassed → system becomes vulnerable.

5.  API Design Not Fully Aligned With Requirements

* Some endpoints didn't reflect real-world role separation clearly.
* USER vs ADMIN responsibilities were mixed.

---

##  FIXES IMPLEMENTED

1.  Secured Admin Endpoints

* Added role check in:

  * PUT /status/{id}
  * DELETE /{id}
* Only ADMIN can perform these actions now.

2.  Enabled USER Loan Filtering by Status

* Modified `/status/{status}` endpoint:

  * ADMIN → gets all loans by status
  * USER → gets ONLY their loans by status

3.  Fixed Username Endpoint Access

* `/user/{username}` now:

  * Accessible ONLY by ADMIN
  * USER gets blocked with SecurityException

4.  Added Repository Support

* New method:
  findByUsernameAndStatus(username, status)

5.  Improved Service Layer Logic

* Added method:
  getByUsernameAndStatus()
* Clean separation of business logic

6.  Strong Ownership Enforcement

* Users cannot:

  * Access others' loan by ID
  * View others' loans by any method

7.  Clean Role-Based Architecture

* USER:

  * Apply loan
  * View own loans
  * Filter own loans
* ADMIN:

  * Full access
  * Manage all loans

---

##  FINAL RESULT

 Secure system (role + ownership based)
 Clean API design
 No data leakage between users
 Production-ready architecture
 Fully aligned with business requirements

---

##  ARCHITECTURE LEVEL IMPROVEMENT

* Moved towards Zero Trust design
* Reduced dependency on client input
* Strengthened backend validation
* Improved maintainability and scalability

---

## END OF DOCUMENT
