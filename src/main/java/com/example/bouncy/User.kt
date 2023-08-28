package com.example.bouncy

class User {
    var user_id: String = ""
    var high_score: Int = 0
    var email: String = ""

    constructor()

    constructor(id: String, score: Int, email: String) {

        this.user_id = id
        this.high_score = score
        this.email = email
    }
}