package com.mcash.data

import com.mcash.data.util.resolveError
import org.junit.Assert.*
import org.junit.Test
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.`is`
import java.net.SocketTimeoutException

class HelperExtensionsTest {
    @Test
    fun resolveError_ThrowableString_ShownError() {
        val error = SocketTimeoutException().resolveError()
        val response = "Connection failed"

        assertEquals(error, response)
        //assertThat(error, `is` response)
    }
}