package com.github.naz013.tasker.arch

import android.content.Context
import android.support.v4.app.Fragment

/**
 * Copyright 2018 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
abstract class BaseFragment : Fragment() {

    protected var navInterface: NavInterface? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (navInterface == null) {
            navInterface = context as NavInterface?
        }
    }

    protected fun moveBack() {
        activity?.onBackPressed()
    }

    open fun canGoBack(): Boolean = true

    open fun onBackStackResume() {
        navInterface?.onFragmentSelect(this)
    }

    override fun onResume() {
        super.onResume()
        onBackStackResume()
    }
}