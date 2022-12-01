package com.gomezrondon.cloudruntest.entities

data class Param(val name:String, val value: String)
data class Payload(val params:List<Param>, val payload: String)