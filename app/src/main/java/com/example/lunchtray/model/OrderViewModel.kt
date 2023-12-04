/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.lunchtray.data.DataSource
import java.text.NumberFormat

class OrderViewModel : ViewModel() {

    // DataSource 中 menuItems 的 Map
    val menuItems = DataSource.menuItems

    /*  小計會在使用者做出選擇時更新 (而不是在最後加總)，因此如果使用者在前往下一個畫面之前變更了所選項目，
        系統就會透過這些變數來追蹤使用者先前的選項  */
    // item prices 的預設值
    private var previousEntreePrice = 0.0 // 先前主餐價格
    private var previousSidePrice = 0.0 // 先前配菜價格
    private var previousAccompanimentPrice = 0.0 // 先前小菜價格

    // tax rate 的預設值
    private val taxRate = 0.08

    // 使用者選取的主餐
    private val _entree = MutableLiveData<MenuItem?>()
    val entree: LiveData<MenuItem?> = _entree

    // 使用者選取的配菜
    private val _side = MutableLiveData<MenuItem?>()
    val side: LiveData<MenuItem?> = _side

    // 使用者選取的小菜
    private val _accompaniment = MutableLiveData<MenuItem?>()
    val accompaniment: LiveData<MenuItem?> = _accompaniment

    // 訂單小計(預設0.0元)
    private val _subtotal = MutableLiveData(0.0)
    // 使用 Transformations.map() 設定 subtotal 顯示的格式(當地幣別)
    val subtotal: LiveData<String> = Transformations.map(_subtotal) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // 訂單總計(預設0.0元)
    private val _total = MutableLiveData(0.0)
    // 使用 Transformations.map() 設定 total 顯示的格式(當地幣別)
    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    // 訂單稅金(預設0.0元)
    private val _tax = MutableLiveData(0.0)
    // 使用 Transformations.map() 設定 tax 顯示的格式(當地幣別)
    val tax: LiveData<String> = Transformations.map(_tax) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Set the entree for the order.
     * 設定訂單的主餐
     */
    fun setEntree(entree: String) {

        /*  1. 如果 _entree 不是 null (也就是使用者已選取主餐，但後來變更了選項)，請將 previousEntreePrice 設為 current _entree 的價格。
            2. 如果 _subtotal 不是 null，請從 subtotal 減去 previousEntreePrice。
            3. 將 _entree 的值更新為傳遞至函式的 entree (使用 menuItems 存取 MenuItem)。
            4. 呼叫 updateSubtotal()，傳遞新選取的主餐價格。  */

        /*  小筆記：?和!!的意思
            ?：做 null check 後，不為空的話再執行
            !!：堅持不會是空值，執行就是了  */
        if (_entree.value!=null) {
            previousEntreePrice = _entree.value!!.price
        }
        if (_subtotal.value!=null) {
            _subtotal.value = _subtotal.value?.minus(previousEntreePrice)
        }
        _entree.value = menuItems[entree]
        _entree.value?.let { updateSubtotal(it.price) }
        Log.i("OrderViewModel","entree value:" + _entree.value.toString())

    }

    /**
     * Set the side for the order.
     * 設定訂單的配菜
     */
    fun setSide(side: String) {

        /*  1. 如果 _side 不是 null (也就是使用者已選取主餐，但後來變更了選項)，請將 previousSidePrice 設為 current _side 的價格。
            2. 如果 _subtotal 不是 null，請從 subtotal 減去 previousSidePrice。
            3. 將 _side 的值更新為傳遞至函式的主餐 (使用 menuItems 存取 MenuItem)。
            4. 呼叫 updateSubtotal()，傳遞新選取的配菜價格。  */

        /*  小筆記：?和!!的意思
            ?：做 null check 後，不為空的話再執行
            !!：堅持不會是空值，執行就是了  */
        if (_side.value!=null) {
            previousSidePrice = _side.value!!.price
        }
        if (_subtotal.value!=null) {
            _subtotal.value = _subtotal.value?.minus(previousSidePrice)
        }
        _side.value = menuItems[side]
        _side.value?.let { updateSubtotal(it.price) }
        Log.i("OrderViewModel","side value:" + _side.value.toString())

    }

    /**
     * Set the accompaniment for the order.
     * 設定訂單的小菜
     */
    fun setAccompaniment(accompaniment: String) {

        /*  1. 如果 _accompaniment 不是 null (也就是使用者已選取主餐，但後來變更了選項)，請將 previousaAcompanimentPrice 設為 current _accompaniment 的價格。
            2. 如果 _subtotal 不是 null，請從 subtotal 減去 previousaccAmpanimentPrice。
            3. 將 _accompaniment 的值更新為傳遞至函式的主餐 (使用 menuItems 存取 MenuItem)。
            4. 呼叫 updateSubtotal()，傳遞新選取的小菜價格。  */

        /*  小筆記：?和!!的意思
            ?：做 null check 後，不為空的話再執行
            !!：堅持不會是空值，執行就是了  */
        if (_accompaniment.value!=null) {
            previousAccompanimentPrice = _accompaniment.value!!.price
        }
        if (_subtotal.value!=null) {
            _subtotal.value = _subtotal.value?.minus(previousAccompanimentPrice)
        }
        _accompaniment.value = menuItems[accompaniment]
        _accompaniment.value?.let { updateSubtotal(it.price) }
        Log.i("OrderViewModel","accompaniment value:" + _accompaniment.value.toString())
    }

    /**
     * Update subtotal value.
     * 呼叫 updateSubtotal()，並加上應加入小計(subtotal)的新價格參數
     */
    private fun updateSubtotal(itemPrice: Double) {

        /*  如果 _subtotal 不是 null，請將 itemPrice 新增至 _subtotal。
            如果 _subtotal 是 null，請將 _subtotal 設為 itemPrice。
            設定或更新 _subtotal 後，呼叫 calculateTaxAndTotal() 即可更新這些值，以反映新的小計(subtotal)。  */

        /*  小筆記：?:的意思
            A ?: B 意思是「當 A 不為 null 時就返回 A，當 A 為 null 時就返回 B」  */
        _subtotal.value = _subtotal.value?.plus(itemPrice) ?: itemPrice
        calculateTaxAndTotal()
        Log.i("OrderViewModel","subtotal:" + _subtotal.value)
    }

    /**
     * Calculate tax and update total.
     * 根據小計(subtotal)來更新稅金(tax)的變數和總金額(total)
     */
    fun calculateTaxAndTotal() {

        /*  將 _tax 設為 tax rate 乘上 subtotal。
            將 _total 設為 subtotal 加上 tax。  */

        _tax.value = taxRate * _subtotal.value!!
        _total.value = _subtotal.value!! + _tax.value!!
        Log.i("OrderViewModel","tax:" + _tax.value)
        Log.i("OrderViewModel","total:" + _total.value)

    }

    /**
     * Reset all values pertaining to the order.
     * 使用者提交或取消訂單時，系統會呼叫 resetOrder()
     */
    fun resetOrder() {

        /*  使用者提交或取消訂單時，系統會呼叫 resetOrder()。當使用者建立新訂單時，請確保應用程式不會留下任何資料。
            建議您將在 OrderViewModel 修改的所有變數設回原始值 (0.0 或空值)，藉此實作 resetOrder()。  */

        _entree.value = null
        _side.value = null
        _accompaniment.value = null
        _subtotal.value = 0.0
        _total.value = 0.0
        _tax.value = 0.0
        previousEntreePrice = 0.0
        previousSidePrice = 0.0
        previousAccompanimentPrice = 0.0

    }
}
