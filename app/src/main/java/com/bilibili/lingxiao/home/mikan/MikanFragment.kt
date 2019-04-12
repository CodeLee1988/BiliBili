package com.bilibili.lingxiao.home.mikan

import android.net.Uri
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.bilibili.lingxiao.R
import com.bilibili.lingxiao.home.mikan.model.MiKanFallData
import com.bilibili.lingxiao.home.mikan.model.MiKanRecommendData
import com.bilibili.lingxiao.utils.ToastUtil
import com.bilibili.lingxiao.utils.UIUtil
import com.camera.lingxiao.common.app.BaseFragment
import kotlinx.android.synthetic.main.fragment_mikan.view.*
import kotlinx.android.synthetic.main.item_mikan_fall.*
import kotlinx.android.synthetic.main.mikan_content_cn.*
import kotlinx.android.synthetic.main.mikan_content_cn.view.*
import kotlinx.android.synthetic.main.mikan_content_jp.view.*
import kotlin.properties.Delegates

class MikanFragment :BaseFragment(),MikanView{
    private var miKanPresenter:MiKanPresenter = MiKanPresenter(this,this)
    private var mCNAdapter:MikanAdapter by Delegates.notNull()
    private var mJPAdapter:MikanAdapter by Delegates.notNull()
    private var mFallAdapter:MiKanFallAdapter by Delegates.notNull()

    private var mCNVideoList = arrayListOf<MiKanRecommendData.Result.RecommendCn.Recommend>()
    private var mJPVideoList = arrayListOf<MiKanRecommendData.Result.RecommendCn.Recommend>()
    private var mEditList = arrayListOf<MiKanFallData.Result>()
    override val contentLayoutId: Int
        get() = R.layout.fragment_mikan

    override fun initInject() {
        super.initInject()
        UIUtil.getUiComponent().inject(this)
    }

    override fun initWidget(root: View) {
        super.initWidget(root)
        var manager:LinearLayoutManager = GridLayoutManager(activity,3)
        mCNAdapter = MikanAdapter(R.layout.item_mikan_video,mCNVideoList)
        mJPAdapter = MikanAdapter(R.layout.item_mikan_video,mJPVideoList)

        root.recycerView.layoutManager = manager
        root.recycerView.adapter = mJPAdapter
        root.recycerView.isNestedScrollingEnabled = false

        var manager_cn:LinearLayoutManager = GridLayoutManager(activity,3)
        root.recycerView_cn.layoutManager = manager_cn
        root.recycerView_cn.adapter = mCNAdapter
        root.recycerView_cn.isNestedScrollingEnabled = false

        mFallAdapter = MiKanFallAdapter(R.layout.item_mikan_fall,mEditList)
        var manager_fall = LinearLayoutManager(activity)
        root.recyclerview_edit.layoutManager = manager_fall
        root.recyclerview_edit.adapter = mFallAdapter

        miKanPresenter.getBanGuMiRecommend()
        miKanPresenter.getBanGuMiFall(0L)

        root.refresh.setOnLoadMoreListener {
            var cursor:Long? = mFallAdapter.data.get(mFallAdapter.itemCount -1).cursor
            if (cursor != null && cursor != 0L)
            miKanPresenter.getBanGuMiFall(cursor)
        }
    }

    override fun onGetMikanRecommend(data: MiKanRecommendData) {
        mCNAdapter.addData(data.result.recommendCn.recommend)
        mJPAdapter.addData(data.result.recommendJp.recommend)
        if (data.result.recommendCn.foot.size > 0){
            mikan_image_cn.setImageURI(Uri.parse(data.result.recommendCn.foot[0].cover))
            title_cn.text =data.result.recommendCn.foot[0].title
            content_cn.text = data.result.recommendCn.foot[0].desc
        }

        if (data.result.recommendJp.foot.size > 0){
            mikan_image.setImageURI(Uri.parse(data.result.recommendJp.foot[0].cover))
            title.text =data.result.recommendJp.foot[0].title
            content.text = data.result.recommendJp.foot[0].desc
        }
    }

    override fun onGetMikanFall(data: MiKanFallData) {
        mFallAdapter.addData(data.result)
    }

    override fun showDialog() {

    }

    override fun diamissDialog() {
    }

    override fun showToast(text: String?) {
        ToastUtil.show(text)
    }
}