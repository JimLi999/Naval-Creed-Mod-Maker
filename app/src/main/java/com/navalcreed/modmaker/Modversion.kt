package com.navalcreed.modmaker

class Modversion {
    private var CaptainVoice:String?=null
    private var GunSound:String?=null

    fun getCaptainVoice():String?{
        return CaptainVoice
    }
    fun getGunSound():String?{
        return GunSound
    }

    fun setCaptainVoice(CaptainVoice:String){
        this.CaptainVoice=CaptainVoice
    }
    fun setGunSound(GunSound:String){
        this.GunSound=GunSound
    }
}