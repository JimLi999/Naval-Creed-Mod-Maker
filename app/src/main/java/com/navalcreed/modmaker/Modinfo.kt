package com.navalcreed.modmaker

class Modinfo {
    private var author:String?=null
    private var minSupportVer:Int=5
    private var modInfo:String?=null
    private var modType:String?=null
    private var name:String?=null
    private var preview:String?=null
    private var hasPreview:Boolean=false
    private var targetVer:Int=6
    private var ver:Long?=null
    fun Modinfo() {}

    fun getAuthor(): String? {
        return author
    }
    fun setAuthor(author: String?) {
        this.author = author
    }

    fun getMinSupportVer():Int{
        return minSupportVer
    }
    fun setMinSupportVer(minSupportVer:Int){
        this.minSupportVer=minSupportVer
    }

    fun getModInfo(): String? {
        return modInfo
    }
    fun setModInfo(modInfo: String?) {
        this.modInfo = modInfo
    }

    fun getModType(): String? {
        return modType
    }
    fun setModType(modType: String?) {
        this.modType = modType
    }

    fun getName(): String? {
        return name
    }
    fun setName(name: String?) {
        this.name = name
    }

    fun getPreview():String?{
        return preview
    }
    fun setPreview(preview:String?){
        this.preview=preview
    }

    fun getHasPreview():Boolean{
        return hasPreview
    }
    fun setHasPreview(hasPreview:Boolean){
        this.hasPreview = hasPreview
    }

    fun getTargetVer():Int{
        return targetVer
    }
    fun setTargetVer(targetVer:Int){
        this.targetVer=targetVer
    }

    fun getVer(): Long? {
        return ver
    }
    fun setVer(ver: Long) {
        this.ver = ver
    }
}