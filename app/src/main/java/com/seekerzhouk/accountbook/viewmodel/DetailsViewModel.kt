package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.room.details.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val myRepository: MyRepository =
        MyRepository.getInstance(application)

    private val config = PagedList.Config.Builder().apply {
        setPageSize(5) // 定义从DataSource每一次加载的项目数。
        setEnablePlaceholders(false) // 是否使用占位符
        setMaxSize(20) // 定义一次保留多少个项目。这可用于通过删除页面来限制保存在内存中的项目数。此值通常是许多页面，因此如果用户向后滚动，则会缓存旧页面。该值必须至少是setPrefetchDistance(int)预取距离的两倍 加上} page size。此约束可防止由于预取而导致连续读取和丢弃负载。
        setInitialLoadSizeHint(10) // 定义首次加载时要加载的项目数。需要是pageSize的两倍及以上。
        setPrefetchDistance(5) // 距离底部还有多少个项目开始预加载
    }.build()

    private fun runInScope(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            run(block)
        }
    }

    fun insertRecords(vararg records: Record) {
        runInScope { myRepository.insertRecords(*records) }
    }

    fun deleteRecords(vararg records: Record) {
        runInScope { myRepository.deleteRecords(*records) }
    }

    fun updateRecords(vararg records: Record) {
        runInScope { myRepository.updateRecords(*records) }
    }

    fun loadAllRecords(): LiveData<PagedList<Record>> {
        return myRepository.loadAllRecords().convertToPagedList(config)
    }

    fun findRecordWithPatten(patten: String): LiveData<PagedList<Record>> {
        return myRepository.findRecordsWithPatten(patten).convertToPagedList(config)
    }

    fun findIncomeRecords(): LiveData<PagedList<Record>> {
        return myRepository.findIncomeRecords().convertToPagedList(config)
    }

    fun findExpendRecords(): LiveData<PagedList<Record>> {
        return myRepository.findExpendRecords().convertToPagedList(config)
    }

    fun findIncomeRecordsWithPatten(patten: String): LiveData<PagedList<Record>> {
        return myRepository.findIncomeRecordsWithPatten(patten).convertToPagedList(config)
    }

    fun findExpendRecordsWithPatten(patten: String): LiveData<PagedList<Record>> {
        return myRepository.findExpendRecordsWithPatten(patten).convertToPagedList(config)
    }

    fun findIncomeRecordsBySelectedType(selectedType: String): LiveData<PagedList<Record>> {
        return myRepository.findIncomeRecordsBySelectedType(selectedType).convertToPagedList(config)
    }

    fun findExpendRecordsBySelectedType(selectedType: String): LiveData<PagedList<Record>> {
        return myRepository.findExpendRecordsBySelectedType(selectedType).convertToPagedList(config)
    }

    fun findIncomeRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<PagedList<Record>> {
        return myRepository.findIncomeRecordsBySelectedTypeWithPatten(selectedType, patten)
            .convertToPagedList(config)
    }


    fun findExpendRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<PagedList<Record>> {
        return myRepository.findExpendRecordsBySelectedTypeWithPatten(
            selectedType,
            patten
        ).convertToPagedList(config)
    }

}

fun DataSource.Factory<Int, Record>.convertToPagedList(config: PagedList.Config): LiveData<PagedList<Record>> {
    return LivePagedListBuilder(this, config).build()
}