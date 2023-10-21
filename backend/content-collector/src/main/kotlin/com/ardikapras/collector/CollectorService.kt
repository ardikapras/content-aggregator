package com.ardikapras.collector

class CollectorService(private val repository: CollectorRepository) {
    fun perform() {
        var listOfNewsSource = repository.getAllActiveNewsSources()
        println("Scheduled task performed! ::---> $listOfNewsSource")
    }
}