APPLICATION SERVICE — API TESTING DOCUMENT

---

##  HEADERS (COMMON)

USER:
X-Auth-Username: user1
X-Auth-Role: USER

ADMIN:
X-Auth-Username: admin
X-Auth-Role: ADMIN

---

##  USER ENDPOINT TESTING

1.  APPLY LOAN
   POST /application/apply

Request Body:
{
"amount": 50000,
"loanType": "PERSONAL",
"purpose": "Medical Emergency"
}

Expected Response:
{
"id": 1,
"username": "user1",
"amount": 50000,
"status": "PENDING",
"loanType": "PERSONAL",
"purpose": "Medical Emergency",
"appliedAt": "2026-03-24T12:00:00",
"remarks": null
}

---

2.  GET MY LOANS
   GET /application/all

Expected:
[
{
"id": 1,
"username": "user1",
"amount": 50000,
"status": "PENDING"
}
]

---

3.  GET MY LOAN BY ID
   GET /application/1

Expected:
{
"id": 1,
"username": "user1",
"amount": 50000,
"status": "PENDING"
}

 Try accessing other user's loan:
GET /application/2

Expected:
403 Forbidden
{
"error": "Access denied. This loan does not belong to you."
}

---

4.  GET MY LOANS BY STATUS
   GET /application/status/PENDING

Expected:
[
{
"id": 1,
"status": "PENDING",
"username": "user1"
}
]

---

5.  USER ACCESS BLOCKED (USERNAME API)
   GET /application/user/user2

Expected:
403 Forbidden

---

6.  USER CANNOT DELETE
   DELETE /application/1

Expected:
403 Forbidden

---

7.  USER CANNOT UPDATE STATUS
   PUT /application/status/1

Expected:
403 Forbidden

---

##  ADMIN ENDPOINT TESTING

1.  GET ALL LOANS
   GET /application/all

Expected:
[
{ "id": 1, "username": "user1" },
{ "id": 2, "username": "user2" }
]

---

2.  GET LOAN BY ID
   GET /application/1

Expected:
{
"id": 1,
"username": "user1"
}

---

3.  GET BY USERNAME
   GET /application/user/user1

Expected:
[
{
"id": 1,
"username": "user1"
}
]

---

4. ✅ GET BY STATUS
   GET /application/status/PENDING

Expected:
[
{ "id": 1 },
{ "id": 2 }
]

---

5.  UPDATE STATUS
   PUT /application/status/1

Request:
{
"status": "APPROVED",
"remarks": "Eligible"
}

Expected:
{
"id": 1,
"status": "APPROVED",
"remarks": "Eligible"
}

---

6.  DELETE LOAN
   DELETE /application/1

Expected:
"Loan application deleted successfully"

---

7.  ADMIN CAN APPLY LOAN
   POST /application/apply

Same as USER — works fine

---

##  FINAL TEST CHECKLIST

 USER cannot see others' data
 USER cannot modify data
 USER can filter own data
 ADMIN has full access
 Security enforced everywhere
 No data leakage

---

## END OF DOCUMENT
