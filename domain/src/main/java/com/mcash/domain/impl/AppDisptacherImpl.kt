package com.mcash.domain.impl

import com.mcash.domain.dispacher.AppDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class AppDisptacherImpl @Inject constructor() : AppDispatcher {
    override val io: CoroutineDispatcher get() = Dispatchers.IO
    override val main: CoroutineDispatcher get() = Dispatchers.Main
}