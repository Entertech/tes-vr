package com.entertech.tes.ble.device

enum class ModeType(val des: String, val command: Byte) {
    TDCS_P("(tDCS 正向", 0x01),
    tDCS_N("(tDCS 负向", 0x02),
    TACS("TACS", 0x03);

    companion object {


        fun getValidType(): List<ModeType> {
            return listOf(TDCS_P, tDCS_N, TACS)
        }

        fun getModeTypeByDes(des: String): ModeType? {
            return when (des) {
                TDCS_P.des -> TDCS_P
                tDCS_N.des -> tDCS_N
                TACS.des -> TACS
                else -> null
            }
        }
    }
}