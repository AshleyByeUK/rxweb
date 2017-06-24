package uk.ashleybye.rxweb.models


data class TflArrival(
        val naptanId: String,
        val platformName: String,
        val towards: String,
        val currentLocation: String,
        val expectedArrival:  String)