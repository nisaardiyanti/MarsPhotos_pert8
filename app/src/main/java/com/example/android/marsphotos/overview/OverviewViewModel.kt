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

package com.example.android.marsphotos.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsphotos.network.MarsApi
import com.example.android.marsphotos.network.MarsPhoto
import kotlinx.coroutines.launch

enum class MarsApiStatus { LOADING, ERROR, DONE }

/**
 * [ViewModel] yang ditampilkan ke [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    // MutableLiveData internal akan menyimpan status permintaan terbaru
    private val _status = MutableLiveData<MarsApiStatus>()

    // LiveData eksternal yang tidak dapat diubah untuk status permintaan
    val status: LiveData<MarsApiStatus> = _status

    // Secara internal, menggunakan MutableLiveData, karena kami akan memperbarui Daftar MarsPhoto
    // dengan nilai baru
    private val _photos = MutableLiveData<List<MarsPhoto>>()

    // Antarmuka LiveData eksternal ke properti tidak dapat diubah, jadi hanya kelas ini yang dapat memodifikasi
    val photos: LiveData<List<MarsPhoto>> = _photos

    /**
     * Fungsi untuk memanggil getMarsPhotos() pada init untuk dapat  menampilkan status.
     */
    init {
        getMarsPhotos()
    }

    /**
     * Digunakan untuk Mendapatkan informasi foto Mars dari layanan Mars API Retrofit dan memperbarui
     * [MarsPhoto] [Daftar] [LiveData].
     */
    private fun getMarsPhotos() {

        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            try {
                _photos.value = MarsApi.retrofitService.getPhotos()
                _status.value = MarsApiStatus.DONE
            } catch (e: Exception) {
                _status.value = MarsApiStatus.ERROR
                _photos.value = listOf()
            }
        }
    }
}
