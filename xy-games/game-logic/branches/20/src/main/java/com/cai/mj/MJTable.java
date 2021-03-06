package com.cai.mj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cai.common.constant.GameConstants;
import com.cai.common.constant.MsgConstants;
import com.cai.common.define.EGoldOperateType;
import com.cai.common.define.EMsgIdType;
import com.cai.common.define.ESysMsgType;
import com.cai.common.domain.AddGoldResultModel;
import com.cai.common.domain.BrandLogModel;
import com.cai.common.domain.CardsData;
import com.cai.common.domain.ChiHuRight;
import com.cai.common.domain.GameRoomRecord;
import com.cai.common.domain.GameRoundRecord;
import com.cai.common.domain.Player;
import com.cai.common.domain.PlayerResult;
import com.cai.common.domain.PlayerStatus;
import com.cai.common.domain.Room;
import com.cai.common.domain.SysParamModel;
import com.cai.common.domain.WeaveItem;
import com.cai.common.util.FvMask;
import com.cai.common.util.RandomUtil;
import com.cai.common.util.ZipUtil;
import com.cai.dictionary.BrandIdDict;
import com.cai.dictionary.SysParamDict;
import com.cai.future.GameSchedule;
import com.cai.future.runnable.DingGuiRunnable;
import com.cai.future.runnable.DispatchCardRunnable;
import com.cai.future.runnable.DispatchLastCardRunnable;
import com.cai.future.runnable.GameFinishRunnable;
import com.cai.future.runnable.JianPaoHuRunnable;
import com.cai.future.runnable.StartLaiGenRunnable;
import com.cai.future.runnable.XiaoHuRunnable;
import com.cai.mj.handler.MJHandler;
import com.cai.mj.handler.MJHandlerChiPeng;
import com.cai.mj.handler.MJHandlerDiHu;
import com.cai.mj.handler.MJHandlerDispatchCard;
import com.cai.mj.handler.MJHandlerFinish;
import com.cai.mj.handler.MJHandlerGang;
import com.cai.mj.handler.MJHandlerHaiDi;
import com.cai.mj.handler.MJHandlerOutCardOperate;
import com.cai.mj.handler.MJHandlerQiShouHongZhong;
import com.cai.mj.handler.MJHandlerTianHu;
import com.cai.mj.handler.MJHandlerYaoHaiDi;
import com.cai.mj.handler.ay.MJHandlerChiPeng_AY;
import com.cai.mj.handler.ay.MJHandlerDispatchCard_AY;
import com.cai.mj.handler.ay.MJHandlerGang_AY;
import com.cai.mj.handler.ay.MJHandlerOutCardBaoTing;
import com.cai.mj.handler.ay.MJHandlerOutCardOperate_AY;
import com.cai.mj.handler.ay.MJHandlerPaoQiang;
import com.cai.mj.handler.cs.MJHandlerChiPeng_CS;
import com.cai.mj.handler.cs.MJHandlerDispatchCard_CS;
import com.cai.mj.handler.cs.MJHandlerGang_CS;
import com.cai.mj.handler.cs.MJHandlerGang_CS_DispatchCard;
import com.cai.mj.handler.cs.MJHandlerHaiDi_CS;
import com.cai.mj.handler.cs.MJHandlerOutCardOperate_CS;
import com.cai.mj.handler.cs.MJHandlerXiaoHu;
import com.cai.mj.handler.cs.MJHandlerYaoHaiDi_CS;
import com.cai.mj.handler.henan.MJHandlerChiPeng_HeNan;
import com.cai.mj.handler.henan.MJHandlerDispatchCard_HeNan;
import com.cai.mj.handler.henan.MJHandlerGang_HeNan;
import com.cai.mj.handler.henan.MJHandlerHun;
import com.cai.mj.handler.henan.MJHandlerOutCardOperate_HeNan;
import com.cai.mj.handler.henan.MJHandlerPao_HeNan;
import com.cai.mj.handler.henan.MJHandlerQiShouHun;
import com.cai.mj.handler.hnhz.MJHandlerChiPeng_HNHZ;
import com.cai.mj.handler.hnhz.MJHandlerDispatchCard_HNHZ;
import com.cai.mj.handler.hnhz.MJHandlerGang_HNHZ;
import com.cai.mj.handler.hnhz.MJHandlerOutCardOperate_HNHZ;
import com.cai.mj.handler.hz.MJHandlerChiPeng_HZ;
import com.cai.mj.handler.hz.MJHandlerDispatchCard_HZ;
import com.cai.mj.handler.hz.MJHandlerGang_HZ;
import com.cai.mj.handler.hz.MJHandlerOutCardOperate_HZ;
import com.cai.mj.handler.lz.MJHandlerChiPeng_LZ;
import com.cai.mj.handler.lz.MJHandlerDispatchCard_LZ;
import com.cai.mj.handler.lz.MJHandlerGang_LZ;
import com.cai.mj.handler.lz.MJHandlerOutCardOperate_LZ;
import com.cai.mj.handler.lz.MJHandlerPao;
import com.cai.mj.handler.sg.MJHandlerChiPeng_SG;
import com.cai.mj.handler.sg.MJHandlerDaDian;
import com.cai.mj.handler.sg.MJHandlerDispatchCard_SG;
import com.cai.mj.handler.sg.MJHandlerGang_SG;
import com.cai.mj.handler.sg.MJHandlerOutCardOperate_SG;
import com.cai.mj.handler.xthh.MJHandlerChiPeng_XTHH;
import com.cai.mj.handler.xthh.MJHandlerDispatchCard_XTHH;
import com.cai.mj.handler.xthh.MJHandlerDispatchLastCard;
import com.cai.mj.handler.xthh.MJHandlerGang_XTHH;
import com.cai.mj.handler.xthh.MJHandlerLaiGen;
import com.cai.mj.handler.xthh.MJHandlerOutCardOperate_XTHH;
import com.cai.mj.handler.zhuzhou.MJHandlerChiPeng_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerDiHu_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerDispatchCard_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerGang_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerGang_ZhuZhou_DispatchCard;
import com.cai.mj.handler.zhuzhou.MJHandlerHaiDi_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerOutCardOperate_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerTianHu_ZhuZhou;
import com.cai.mj.handler.zhuzhou.MJHandlerYaoHaiDi_ZhuZhou;
import com.cai.mj.handler.zz.MJHandlerChiPeng_ZZ;
import com.cai.mj.handler.zz.MJHandlerDispatchCard_ZZ;
import com.cai.mj.handler.zz.MJHandlerGang_ZZ;
import com.cai.mj.handler.zz.MJHandlerOutCardOperate_ZZ;
import com.cai.service.MongoDBServiceImpl;
import com.cai.service.PlayerServiceImpl;

import protobuf.clazz.Protocol;
import protobuf.clazz.Protocol.GameEndResponse;
import protobuf.clazz.Protocol.GameStartResponse;
import protobuf.clazz.Protocol.Int32ArrayResponse;
import protobuf.clazz.Protocol.LocationInfor;
import protobuf.clazz.Protocol.MsgAllResponse;
import protobuf.clazz.Protocol.PlayerResultResponse;
import protobuf.clazz.Protocol.Response;
import protobuf.clazz.Protocol.Response.ResponseType;
import protobuf.clazz.Protocol.RoomInfo;
import protobuf.clazz.Protocol.RoomPlayerResponse;
import protobuf.clazz.Protocol.RoomResponse;
import protobuf.clazz.Protocol.TableResponse;
import protobuf.clazz.Protocol.WeaveItemResponse;
import protobuf.clazz.Protocol.WeaveItemResponseArrayResponse;

//效验类型
class EstimatKind {
	public static int EstimatKind_OutCard = 1; // 出牌效验
	public static int EstimatKind_GangCard = 2; // 杠牌效验
}

///////////////////////////////////////////////////////////////////////////////////////////////
public class MJTable extends Room {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7060061356475703643L;

	private static Logger logger = Logger.getLogger(MJTable.class);

	/**
	 * 这个值 不能在这里改--这个参数开发用 通过 命令行改牌
	 */
	public static boolean DEBUG_CARDS_MODE = false;

	/**
	 * 这个值 不能在这里改--这个参数测试用 通过后台改牌 改单个桌子的
	 */
	public boolean BACK_DEBUG_CARDS_MODE = false;
	/**
	 * 后台操作临时数组
	 */
	public int debug_my_cards[];

	public int _game_type_index; // 游戏类型
	public int _game_rule_index; // 游戏规则
	public int _game_round; // 局数
	public int _cur_round; // 局数
	public int _player_ready[]; // 准备
	public float _di_fen; // 底分

	public int _game_status;

	// 状态变量
	private boolean _status_send; // 发牌状态
	private boolean _status_gang; // 抢杆状态
	private boolean _status_cs_gang; // 长沙杆状态
	private boolean _status_gang_hou_pao; // 杠后炮状态

	public PlayerResult _player_result;

	// 运行变量
	public int _provide_card = GameConstants.INVALID_VALUE; // 供应扑克
	public int _resume_player = GameConstants.INVALID_SEAT; // 还原用户
	public int _current_player = GameConstants.INVALID_SEAT; // 当前用户
	public int _provide_player = GameConstants.INVALID_SEAT; // 供应用户

	// 用户状态
	// boolean _response[]; // 响应标志
	// int _player_action[]; // 用户动作
	// int _operate_card[]; // 操作扑克
	// int _perform_action[]; // 执行动作
	// int _player_status[]; // 执行动作

	public PlayerStatus _playerStatus[];

	// 发牌信息
	public int _send_card_data = GameConstants.INVALID_VALUE; // 发牌扑克

	public CardsData _gang_card_data;

	public int _send_card_count = GameConstants.INVALID_VALUE; // 发牌数目
	// public int _repertory_card_cs[]; // 长沙库存扑克 = new
	// // int[MJGameConstants.CARD_COUNT_CS]
	// public int _repertory_card_zz[]; // 转转库存扑克 = new
	// // int[MJGameConstants.CARD_COUNT_ZZ]
	//

	public int _repertory_card[];

	public int _qiang_max_count; // 最大呛分
	public int _lian_zhuang_player; // 连庄玩家
	public int _shang_zhuang_player; // 上庄玩家

	// 出牌信息
	public int _out_card_player = GameConstants.INVALID_SEAT; // 出牌用户
	public int _out_card_data = GameConstants.INVALID_VALUE; // 出牌扑克
	public int _out_card_count = GameConstants.INVALID_VALUE; // 出牌数目
	public int _all_card_len = 0;

	GameRoomRecord _gameRoomRecord;
	BrandLogModel _recordRoomRecord;

	public GameRoundRecord GRR;

	public MJGameLogic _logic = null;

	/**
	 * 当前牌桌的庄家
	 */
	public int _banker_select = GameConstants.INVALID_SEAT;

	private long _request_release_time;
	private ScheduledFuture _release_scheduled;
	private ScheduledFuture _table_scheduled;

	public MJHandler _handler;

	public MJHandlerDispatchCard _handler_dispath_card;
	public MJHandlerOutCardOperate _handler_out_card_operate;
	public MJHandlerGang _handler_gang;
	public MJHandlerChiPeng _handler_chi_peng;
	public MJHandlerHaiDi _handler_hai_di;
	public MJHandlerYaoHaiDi _handler_yao_hai_di;
	public MJHandlerTianHu _handler_tianhu;
	public MJHandlerDiHu _handler_dihu;

	public MJHandlerXiaoHu _handler_xiao_hu;
	public MJHandlerGang_CS_DispatchCard _handler_gang_cs;

	public MJHandlerQiShouHongZhong _handler_qishou_hongzhong;
	public MJHandlerDaDian _handler_da_dian;
	public MJHandlerLaiGen _handler_lai_gen;
	public MJHandlerDispatchLastCard _handler_dispath_last_card;

	// 株洲
	public MJHandlerGang_ZhuZhou_DispatchCard _handler_gang_zhuzhou;

	public MJHandlerOutCardBaoTing _handler_out_card_bao_ting; // 报听

	public MJHandlerPaoQiang _handler_pao_qiang; // 跑呛
	public MJHandlerPao _handler_pao; // 跑

	public MJHandlerPao_HeNan _handler_pao_henna;// 河南跑
	public MJHandlerQiShouHun _handler_qishou_hun;// 河南4混
	public MJHandlerHun _handler_hun;// 河南选混

	public MJHandlerFinish _handler_finish; // 结束
	
	private int cost_dou=0;//扣豆

	public MJTable() {
		super(RoomType.MJ);

		_logic = new MJGameLogic();

		// 设置等待状态

		// 玩家
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			get_players()[i] = null;

		}
		_game_status = GameConstants.GS_MJ_FREE;
		// 游戏变量
		_player_ready = new int[GameConstants.GAME_PLAYER];
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		// 出牌信息
		_out_card_data = 0;

		_status_cs_gang = false;
		// 结束信息

		// 用户状态
		// _response = new boolean[MJGameConstants.GAME_PLAYER];
		// _player_action = new int[MJGameConstants.GAME_PLAYER];
		// _operate_card = new int[MJGameConstants.GAME_PLAYER];
		// _perform_action = new int[MJGameConstants.GAME_PLAYER];
		// _player_status = new int[MJGameConstants.GAME_PLAYER];
		//
		// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
		// _player_action[i] = MJGameConstants.WIK_NULL;
		// }

		_gang_card_data = new CardsData(GameConstants.MAX_COUNT);
	}

	public void init_table(int game_type_index, int game_rule_index, int game_round) {
		_game_type_index = game_type_index;//
		// _game_type_index = MJGameConstants.GAME_TYPE_XTHH;//

		int dddd = FvMask.mask(GameConstants.GAME_RULE_HEBEI_DI_FEN_10);
		dddd |= FvMask.mask(GameConstants.GAME_RULE_HEBEI_YI_LAI_DAO_DI);
		_game_rule_index = game_rule_index;
		// _game_rule_index =dddd;//;
		_game_round = game_round;

		_cur_round = 0;

		_player_result = new PlayerResult(this.getRoom_owner_account_id(), this.getRoom_id(), _game_type_index,
				_game_rule_index, _game_round, this.get_game_des());

		if (is_mj_type(GameConstants.GAME_TYPE_CS)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_CS();
			_handler_out_card_operate = new MJHandlerOutCardOperate_CS();
			_handler_gang = new MJHandlerGang_CS();
			_handler_chi_peng = new MJHandlerChiPeng_CS();

			// 长沙麻将
			_handler_xiao_hu = new MJHandlerXiaoHu();
			_handler_gang_cs = new MJHandlerGang_CS_DispatchCard();
			_handler_hai_di = new MJHandlerHaiDi_CS();
			_handler_yao_hai_di = new MJHandlerYaoHaiDi_CS();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_HZ();
			_handler_out_card_operate = new MJHandlerOutCardOperate_HZ();
			_handler_gang = new MJHandlerGang_HZ();
			_handler_chi_peng = new MJHandlerChiPeng_HZ();

			_handler_qishou_hongzhong = new MJHandlerQiShouHongZhong();

		} else if (is_mj_type(GameConstants.GAME_TYPE_ZZ)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_ZZ();
			_handler_out_card_operate = new MJHandlerOutCardOperate_ZZ();
			_handler_gang = new MJHandlerGang_ZZ();
			_handler_chi_peng = new MJHandlerChiPeng_ZZ();

		} else if (is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_SG();
			_handler_out_card_operate = new MJHandlerOutCardOperate_SG();
			_handler_gang = new MJHandlerGang_SG();
			_handler_chi_peng = new MJHandlerChiPeng_SG();
			_handler_da_dian = new MJHandlerDaDian();

		} else if (is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_ZhuZhou();
			_handler_out_card_operate = new MJHandlerOutCardOperate_ZhuZhou();
			_handler_gang = new MJHandlerGang_ZhuZhou();
			_handler_chi_peng = new MJHandlerChiPeng_ZhuZhou();

			_handler_gang_zhuzhou = new MJHandlerGang_ZhuZhou_DispatchCard();
			_handler_hai_di = new MJHandlerHaiDi_ZhuZhou();
			_handler_yao_hai_di = new MJHandlerYaoHaiDi_ZhuZhou();
			_handler_tianhu = new MJHandlerTianHu_ZhuZhou();
			_handler_dihu = new MJHandlerDiHu_ZhuZhou();

		} else if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_XTHH();
			_handler_out_card_operate = new MJHandlerOutCardOperate_XTHH();
			_handler_gang = new MJHandlerGang_XTHH();
			_handler_chi_peng = new MJHandlerChiPeng_XTHH();

			_handler_lai_gen = new MJHandlerLaiGen();
			_handler_dispath_last_card = new MJHandlerDispatchLastCard();
			if (has_rule(GameConstants.GAME_RULE_HEBEI_DI_FEN_05)) {
				this._di_fen = 0.5f;
			} else if (has_rule(GameConstants.GAME_RULE_HEBEI_DI_FEN_10)) {
				this._di_fen = 1;
			} else if (has_rule(GameConstants.GAME_RULE_HEBEI_DI_FEN_20)) {
				this._di_fen = 2;
			}
			//
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_AY();
			_handler_out_card_operate = new MJHandlerOutCardOperate_AY();
			_handler_gang = new MJHandlerGang_AY();
			_handler_chi_peng = new MJHandlerChiPeng_AY();

			_handler_out_card_bao_ting = new MJHandlerOutCardBaoTing();
			_handler_pao_qiang = new MJHandlerPaoQiang();

			_qiang_max_count = 0;
			_lian_zhuang_player = GameConstants.INVALID_SEAT;
			this._shang_zhuang_player = GameConstants.INVALID_SEAT;

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_player_result.pao[i] = 0;
				_player_result.qiang[i] = 0;
			}
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_LZ();
			_handler_out_card_operate = new MJHandlerOutCardOperate_LZ();
			_handler_gang = new MJHandlerGang_LZ();
			_handler_chi_peng = new MJHandlerChiPeng_LZ();

			_handler_pao = new MJHandlerPao();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_player_result.pao[i] = 0;
			}
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_HeNan();
			_handler_out_card_operate = new MJHandlerOutCardOperate_HeNan();
			_handler_gang = new MJHandlerGang_HeNan();
			_handler_chi_peng = new MJHandlerChiPeng_HeNan();

			_handler_qishou_hun = new MJHandlerQiShouHun();
			_handler_pao_henna = new MJHandlerPao_HeNan();
			_handler_hun = new MJHandlerHun();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_player_result.pao[i] = 0;
			}
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
			// 初始化基础牌局handler
			_handler_dispath_card = new MJHandlerDispatchCard_HNHZ();
			_handler_out_card_operate = new MJHandlerOutCardOperate_HNHZ();
			_handler_gang = new MJHandlerGang_HNHZ();
			_handler_chi_peng = new MJHandlerChiPeng_HNHZ();

			_handler_qishou_hongzhong = new MJHandlerQiShouHongZhong();
		}

		_handler_finish = new MJHandlerFinish();

		if (is_mj_type(GameConstants.GAME_TYPE_HZ) || is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ))// 红中
		// 红中玩法is_mj_type(MJGameConstants.GAME_TYPE_HZ)
		{
			// 设置红中为癞子
			_logic.add_magic_card_index(_logic.switch_to_card_index(GameConstants.HZ_MAGIC_CARD));
		} else {
			// _logic.add_magic_card_index(MJGameConstants.MAX_INDEX);
		}
	}

	public boolean has_rule(int cbRule) {
		return FvMask.has_any(_game_rule_index, FvMask.mask(cbRule));
	}

	////////////////////////////////////////////////////////////////////////
	public boolean reset_init_data() {
		int game_type_index = this._game_type_index;
		int index = 0;
		if (game_type_index == GameConstants.GAME_TYPE_ZZ) {
			this.setGame_id(GameConstants.GAME_ID_HUNAN);
			index = 1201;
		} else if (game_type_index == GameConstants.GAME_TYPE_CS) {
			this.setGame_id(GameConstants.GAME_ID_HUNAN);
			index = 1202;
		} else if (game_type_index == GameConstants.GAME_TYPE_HZ) {
			this.setGame_id(GameConstants.GAME_ID_HUNAN);
			index = 1203;
		} else if (game_type_index == GameConstants.GAME_TYPE_SHUANGGUI) {
			this.setGame_id(GameConstants.GAME_ID_HUNAN);
			index = 1204;
		} else if (game_type_index == GameConstants.GAME_TYPE_ZHUZHOU) {
			this.setGame_id(GameConstants.GAME_ID_HUNAN);
			index = 1205;
		} else if (game_type_index == GameConstants.GAME_TYPE_XTHH) {
			this.setGame_id(GameConstants.GAME_ID_HUBEI);
			index = 1206;
		} else if (game_type_index == GameConstants.GAME_TYPE_HENAN_AY) {
			this.setGame_id(GameConstants.GAME_ID_HENAN);
			index = 1207;
		} else if (game_type_index == GameConstants.GAME_TYPE_HENAN_LZ) {
			this.setGame_id(GameConstants.GAME_ID_HENAN);
			index = 1208;
		} else if (game_type_index == GameConstants.GAME_TYPE_HENAN) {
			this.setGame_id(GameConstants.GAME_ID_HENAN);
			index = 1209;
		} else if (game_type_index == GameConstants.GAME_TYPE_HENAN_HZ) {
			this.setGame_id(GameConstants.GAME_ID_HENAN);
			index = 1211;
		} else if (game_type_index == GameConstants.GAME_TYPE_SXG) {
			this.setGame_id(GameConstants.GAME_ID_HENGYANG);
			index = 1204;
		}

		if (_cur_round == 0) {
			// 判断房卡
			SysParamModel sysParamModel1010 = SysParamDict.getInstance()
					.getSysParamModelDictionaryByGameId(this.getGame_id()).get(1010);
			SysParamModel sysParamModel1011 = SysParamDict.getInstance()
					.getSysParamModelDictionaryByGameId(this.getGame_id()).get(1011);
			SysParamModel sysParamModel1012 = SysParamDict.getInstance()
					.getSysParamModelDictionaryByGameId(this.getGame_id()).get(1012);
			int check_gold = 0;
			boolean create_result = true;

			if (_game_round == 4) {
				check_gold = sysParamModel1010.getVal2();
			} else if (_game_round == 8) {
				check_gold = sysParamModel1011.getVal2();
			} else if (_game_round == 16) {
				check_gold = sysParamModel1012.getVal2();
			}

			// if(is_mj_type(MJGameConstants.GAME_TYPE_HENAN_AY) ||
			// is_mj_type(MJGameConstants.GAME_TYPE_HENAN_LZ) ||
			// is_mj_type(MJGameConstants.GAME_TYPE_HENAN)){//安阳麻将特殊处理一下
			// if(_game_round==4){
			// check_gold = sysParamModel1011.getVal2();
			// }else if(_game_round==8){
			// check_gold = sysParamModel1012.getVal2();
			// }else if(_game_round==16){
			// check_gold = sysParamModel1012.getVal2();
			// }
			//
			// }else{
			// if(_game_round==8){
			// check_gold = sysParamModel1011.getVal2();
			// }else if(_game_round==16){
			// check_gold = sysParamModel1012.getVal2();
			// }
			//
			// }

			// 注意游戏ID不一样
			if (check_gold == 0) {
				create_result = false;
			} else {
				// 是否免费的
				SysParamModel sysParamModel = SysParamDict.getInstance()
						.getSysParamModelDictionaryByGameId(this.getGame_id()).get(index);

				if (sysParamModel != null && sysParamModel.getVal2() == 1) {
					//收费
					StringBuilder buf = new StringBuilder();
					buf.append("创建房间:" + this.getRoom_id()).append("game_id:" + this.getGame_id())
							.append(",game_type_index:" + game_type_index).append(",game_round:" + _game_round);
					AddGoldResultModel result = PlayerServiceImpl.getInstance().subGold(this.getRoom_owner_account_id(),
							check_gold, false, buf.toString());
					if (result.isSuccess() == false) {
						create_result = false;
					}else{
						//扣豆成功
						cost_dou = check_gold;
					}
				}

			}
			if (create_result == false) {
				RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
				roomResponse.setType(MsgConstants.RESPONSE_FORCE_EXIT);
				Player player = null;
				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					this.send_response_to_player(i, roomResponse);

					player = this.get_players()[i];
					if (player == null)
						continue;
					if (i == 0) {
						send_error_notify(i, 1, "闲逸豆不足,游戏解散");
					} else {
						send_error_notify(i, 1, "创建人闲逸豆不足,游戏解散");
					}

				}

				// 删除房间
				PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
				return false;
			}
			
			

			/*
			 * || is_mj_type(MJGameConstants.GAME_TYPE_HENAN_AY) ||
			 * is_mj_type(MJGameConstants.GAME_TYPE_HENAN_LZ)
			 */
			if (is_mj_type(GameConstants.GAME_TYPE_CS)) {
				this.shuffle_players();

				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					this.get_players()[i].set_seat_index(i);
					if (this.get_players()[i].getAccount_id() == this.getRoom_owner_account_id()) {
						this._banker_select = i;
					}
				}
			}

			if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
				int banker = RandomUtil.getRandomNumber(GameConstants.GAME_PLAYER);
				this._banker_select = banker;
			}

			record_game_room();
		}

		// 设置变量
		_provide_card = GameConstants.INVALID_VALUE;
		_out_card_data = GameConstants.INVALID_VALUE;
		_send_card_data = GameConstants.INVALID_VALUE;

		_provide_player = GameConstants.INVALID_SEAT;
		_out_card_player = GameConstants.INVALID_SEAT;
		_current_player = GameConstants.INVALID_SEAT;

		_send_card_count = 0;
		_out_card_count = 0;

		GRR = new GameRoundRecord();
		GRR._start_time = System.currentTimeMillis() / 1000L;
		GRR._game_type_index = _game_type_index;
		GRR._cur_round = _cur_round;

		// 新建
		_playerStatus = new PlayerStatus[4];
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_playerStatus[i] = new PlayerStatus();
		}

		// _cur_round=8;
		_cur_round++;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_playerStatus[i].reset();
		}

		// 牌局回放
		GRR._room_info.setRoomId(this.getRoom_id());
		GRR._room_info.setGameRuleIndex(_game_rule_index);
		GRR._room_info.setGameRuleDes(get_game_des());
		GRR._room_info.setGameTypeIndex(_game_type_index);
		GRR._room_info.setGameRound(_game_round);
		GRR._room_info.setCurRound(_cur_round);
		GRR._room_info.setGameStatus(_game_status);
		GRR._room_info.setCreatePlayerId(this.getRoom_owner_account_id());

		Player rplayer;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			rplayer = this.get_players()[i];
			if (rplayer == null)
				continue;
			RoomPlayerResponse.Builder room_player = RoomPlayerResponse.newBuilder();
			room_player.setAccountId(rplayer.getAccount_id());
			room_player.setHeadImgUrl(rplayer.getAccount_icon());
			room_player.setIp(rplayer.getAccount_ip());
			room_player.setUserName(rplayer.getNick_name());
			room_player.setSeatIndex(rplayer.get_seat_index());
			room_player.setOnline(rplayer.isOnline() ? 1 : 0);
			room_player.setIpAddr(rplayer.getAccount_ip_addr());
			room_player.setSex(rplayer.getSex());
			room_player.setScore(_player_result.game_score[i]);
			room_player.setReady(_player_ready[i]);
			if(rplayer.locationInfor!=null) {
				room_player.setLocationInfor(rplayer.locationInfor);
			}
			GRR._video_recode.addPlayers(room_player);
		}

		GRR._video_recode.setBankerPlayer(this._banker_select);

		return true;
	}

	// 游戏开始
	public boolean handler_game_start() {

		reset_init_data();

		// 庄家选择
		this.progress_banker_select();

		_game_status = GameConstants.GS_MJ_PLAY;

		GRR._banker_player = _banker_select;
		_current_player = GRR._banker_player;

		//
		if (is_mj_type(GameConstants.GAME_TYPE_HZ))// 红中玩法
		{
			_repertory_card = new int[GameConstants.CARD_COUNT_HZ];
			shuffle(_repertory_card, GameConstants.CARD_DATA_HZ);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			// 安阳麻将
			if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
				_repertory_card = new int[GameConstants.CARD_COUNT_DAI_FENG_AY];
				shuffle(_repertory_card, GameConstants.CARD_DATA_DAI_FENG_AY);
			} else {
				_repertory_card = new int[GameConstants.CARD_COUNT_BU_DAI_FENG_AY];
				shuffle(_repertory_card, GameConstants.CARD_DATA_BU_DAI_FENG_AY);
			}
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)) {
			// 林州
			if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
				_repertory_card = new int[GameConstants.CARD_COUNT_DAI_FENG_LZ];
				shuffle(_repertory_card, GameConstants.CARD_DATA_DAI_FENG_LZ);
			} else {
				_repertory_card = new int[GameConstants.CARD_COUNT_BU_DAI_FENG_LZ];
				shuffle(_repertory_card, GameConstants.CARD_DATA_BU_DAI_FENG_LZ);
			}
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			// 河南通用麻将
			if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
				_repertory_card = new int[GameConstants.CARD_COUNT_DAI_FENG_LZ];
				shuffle(_repertory_card, GameConstants.CARD_DATA_DAI_FENG_LZ);
			} else {
				_repertory_card = new int[GameConstants.CARD_COUNT_BU_DAI_FENG_LZ];
				shuffle(_repertory_card, GameConstants.CARD_DATA_BU_DAI_FENG_LZ);
			}
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
			// 河南红中麻将
			if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
				_repertory_card = new int[GameConstants.CARD_COUNT_DAI_FENG_HNHZ];
				shuffle(_repertory_card, GameConstants.CARD_DATA_DAI_FENG_LZ);
			} else {
				_repertory_card = new int[GameConstants.CARD_COUNT_BU_DAI_FENG_HNHZ];
				shuffle(_repertory_card, GameConstants.CARD_DATA_HZ);
			}
		} else {
			// 晃晃麻将也可以用湖南麻将的牌
			_repertory_card = new int[GameConstants.CARD_COUNT_HU_NAN];
			shuffle(_repertory_card, GameConstants.CARD_DATA_HU_NAN);
		}

		if (DEBUG_CARDS_MODE || BACK_DEBUG_CARDS_MODE)
			test_cards();

		if (is_mj_type(GameConstants.GAME_TYPE_ZZ)) {
			return game_start_zz();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			return game_start_hz();
		} else if (is_mj_type(GameConstants.GAME_TYPE_CS)) {
			return game_start_cs();
		} else if (is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
			return game_start_sg();
		} else if (is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			return game_start_zhuzhou();
		} else if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {
			return game_start_xthh();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			return game_start_ay();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)) {
			return game_start_lz();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			return game_start_henan();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
			return game_start_hnhz();
		}
		return false;
	}

	/// 洗牌
	private void shuffle(int repertory_card[], int mj_cards[]) {
		_all_card_len = repertory_card.length;
		GRR._left_card_count = _all_card_len;

		_logic.random_card_data(repertory_card, mj_cards);

		int send_count;
		int have_send_count = 0;

		// 分发扑克
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// if(GRR._banker_player == i){
			// send_count = MJGameConstants.MAX_COUNT;
			// }else{
			//
			// send_count = (MJGameConstants.MAX_COUNT - 1);
			// }
			send_count = (GameConstants.MAX_COUNT - 1);
			GRR._left_card_count -= send_count;

			// 一人13张牌,庄家多一张
			_logic.switch_to_cards_index(repertory_card, have_send_count, send_count, GRR._cards_index[i]);

			have_send_count += send_count;

		}
		// 记录初始牌型
		_recordRoomRecord.setBeginArray(Arrays.toString(repertory_card));

	}

	private void test_cards() {
		// int cards[] = new int[] { 0x05,0x06, 0x09, 0x09, 0x12,0x12, 0x17,
		// 0x18, 0x19, 0x21, 0x22, 0x23, 0x09 };
		// int cards[] = new int[] { 0x04,0x04, 0x05, 0x05, 0x05,0x07, 0x08,
		// 0x09, 0x14, 0x15, 0x19, 0x19, 0x35 };
		// 双鬼5个癞子不胡
		// int cards[] = new int[] { 0x05,0x06, 0x07, 0x13, 0x14,0x17, 0x19,
		// 0x19, 0x19, 0x21, 0x23, 0x24, 0x25 };

		// 黑摸
		// int cards[] = new int[] { 0x06, 0x07, 0x08, 0x14, 0x15,0x16, 0x16,
		// 0x24, 0x25, 0x26, 0x27, 0x28, 0x29 };

		// 晃晃不能胡
		// int cards[] = new int[] { 0x12, 0x13, 0x16, 0x17, 0x18,0x22, 0x22,
		// 0x24, 0x25, 0x26, 0x28, 0x28, 0x28 };

		// 晃晃没显示听癞子
		// int cards[] = new int[] { 0x02, 0x04, 0x06, 0x06, 0x06,0x14, 0x15,
		// 0x16, 0x21, 0x21, 0x21, 0x22, 0x22 };

		// 双鬼没显示听鬼
		// int cards[] = new int[] { 0x01, 0x02, 0x15, 0x15, 0x16,0x17, 0x18,
		// 0x26, 0x26, 0x26, 0x27, 0x28, 0x29 };

		// int cards[] = new int[] { 0x01, 0x01, 0x01, 0x05, 0x06, 0x07,0x08,
		// 0x12, 0x13, 0x14, 0x17, 0x18, 0x19 };
		// int cards[] = new int[] { 0x01, 0x01, 0x01, 0x05, 0x06, 0x07,0x08,
		// 0x12, 0x13, 0x14, 0x17, 0x18, 0x19 };
		// int cards[] = new int[] { 0x01, 0x01, 0x01, 0x05, 0x06, 0x07,0x08,
		// 0x12, 0x13, 0x14, 0x17, 0x18, 0x19 };

		// 红中显示听多牌
		// int cards[] = new int[]
		// {0x02,0x03,0x04,0x07,0x08,0x09,0x11,0x11,0x11,0x18,0x18,0x25,0x26};
		// 杠5饼出错
		// int cards[] = new int[]
		// {0x02,0x02,0x06,0x07,0x22,0x22,0x22,0x25,0x25,0x02,0x27,0x28,0x29};

		// 五鬼不能胡
		// int cards[] = new int[]
		// {0x01,0x01,0x03,0x03,0x06,0x06,0x07,0x15,0x16,0x18,0x18,0x25,0x26};

		// 起手四红中
		// int cards[] = new int[]
		// {0x11,0x12,0x12,0x13,0x14,0x16,0x16,0x17,0x18,0x35,0x35,0x35,0x35};
		// 红中不能胡1
		// int cards[] = new int[]
		// {0x11,0x12,0x12,0x13,0x14,0x16,0x16,0x17,0x18,0x21,0x22,0x23,0x35};
		// 红中不能胡2
		// int cards[] = new int[]
		// {0x01,0x02,0x03,0x17,0x17,0x21,0x22,0x22,0x23,0x24,0x26,0x28,0x35};
		// 杠牌重复/
		// int cards[] = new int[] { 0x09, 0x09, 0x18, 0x18, 0x19, 0x19, 0x19,
		// 0x26, 0x27, 0x28, 0x29, 0x29, 0x29 };

		// 花牌
		// int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};
		// int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};

		// int cards[] = new int[]
		// {0x05,0x05,0x07,0x07,0x08,0x09,0x09,0x17,0x17,0x18,0x18,0x19,0x19};
		// int cards[] = new int[]
		// {0x01,0x02,0x03,0x04,0x05,0x06,0x16,0x16,0x21,0x21,0x21,0x15,0x15};

		// int cards[] = new int[]
		// {0x01,0x02,0x03,0x04,0x05,0x06,0x16,0x16,0x21,0x21,0x21,0x15,0x15};
		// int cards[] = new int[]
		// {0x03,0x03,0x05,0x05,0x06,0x07,0x08,0x08,0x08,0x12,0x12,0x12,0x22};
		// int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};
		// int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};
		// //int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};
		// int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};
		// int cards[] = new int[]
		// {0x09,0x09,0x18,0x18,0x19,0x19,0x19,0x26,0x27,0x28,0x29,0x29,0x38};

		// 单吊
		// int cards[] = new int[]
		// {0x01,0x01,0x01,0x02,0x02,0x02,0x03,0x03,0x03,0x04,0x04,0x04,0x06};
		// int cards[] = new int[]
		// {0x01,0x01,0x01,0x02,0x02,0x02,0x03,0x03,0x03,0x04,0x04,0x04,0x06};
		// int cards[] = new int[]
		// {0x01,0x01,0x01,0x02,0x02,0x02,0x03,0x03,0x03,0x04,0x04,0x04,0x06};

		// 全球人
		 int cards[] = new int[] {0x13,0x14,0x15,0x16,0x17,0x17,0x17,0x18,0x18,0x18,0x19,0x19,0x19};

		
		//河南麻将七小对
		//int cards[] = new int[] {0x02,0x02,0x05,0x05,0x09,0x09,0x12,0x12,0x14,0x14,0x18,0x27,0x27};
		// 吃
		// int cards[] = new int[]
		// {0x01,0x02,0x05,0x05,0x21,0x23,0x24,0x26,0x26,0x27,0x28,0x29,0x29};

		// 板板胡
		// int cards[] = new int[]
		// {0x03,0x14,0x16,0x16,0x17,0x17,0x17,0x21,0x21,0x23,0x23,0x19,0x19};
		// 红中中2鸟算分不对
		// int cards[] = new int[]
		// {0x07,0x09,0x12,0x12,0x15,0x17,0x21,0x21,0x21,0x23,0x23,0x23,0x35};

		// 杠牌胡两张
		// int cards[] = new int[]
		// {0x04,0x06,0x06,0x06,0x03,0x03,0x03,0x08,0x08,0x08,0x09,0x09,0x09};
		// 八万不能杠
		// int cards[] = new int[]
		// {0x01,0x06,0x06,0x06,0x03,0x04,0x05,0x08,0x08,0x08,0x09,0x09,0x09};
		// int cards[] = new int[]
		// {0x01,0x06,0x06,0x06,0x03,0x04,0x05,0x08,0x08,0x08,0x09,0x09,0x09};
		// int cards[] = new int[]
		// {0x01,0x06,0x06,0x06,0x03,0x04,0x05,0x08,0x08,0x08,0x09,0x09,0x09};

		// int cards[] = new int[]
		// {0x01,0x02,0x04,0x04,0x05,0x07,0x07,0x08,0x14,0x19,0x21,0x23,0x29};
		// 四喜
//		 int cards[] = new int[] { 0x11, 0x11, 0x11, 0x11, 0x14, 0x14, 0x15,
//		 0x15, 0x17, 0x17, 0x19, 0x19, 0x19 };
//		 int cards[] = new int[] { 0x11, 0x11, 0x11, 0x11, 0x14, 0x14, 0x15,
//		 0x15, 0x17, 0x17, 0x19, 0x19, 0x19 };
		// int cards[] = new int[] { 0x11, 0x11, 0x11, 0x11, 0x14, 0x14, 0x15,
		// 0x15, 0x17, 0x17, 0x19,
		//
		// 0x19, 0x19 };
		// int cards[] = new int[] { 0x15, 0x19, 0x13, 0x02, 0x04, 0x03, 0x15,
		// 0x15, 0x17, 0x17, 0x19,0x17, 0x19 };
	//	int cards[] = new int[] { 0x11, 0x11, 0x11, 0x11, 0x14, 0x14, 0x15, 0x15, 0x17, 0x17, 0x19, 0x19, 0x19 };
		// int cards[] = new int[] { 0x15, 0x19, 0x13, 0x02, 0x04, 0x03, 0x15,
		// 0x15, 0x17, 0x17, 0x19,0x17, 0x19 };
		// //碰碰胡
		// int cards[] = new int[] { 0x02, 0x02, 0x02, 0x03, 0x03,0x11, 0x11,
		// 0x11, 0x15, 0x15, 0x21, 0x21, 0x21 };

		// 清一色碰碰胡
		// int cards[] = new int[] { 0x02, 0x02, 0x02, 0x02, 0x01,0x01, 0x01,
		// 0x01, 0x05, 0x05, 0x03, 0x06, 0x06 };

		// int cards[] = new int[] { 0x08, 0x08, 0x08, 0x01, 0x01, 0x01, 0x05,
		// 0x05, 0x05, 0x02, 0x02, 0x06, 0x06 };

		// 清一色双龙七
		// int cards[] = new int[] { 0x02, 0x02, 0x02, 0x02, 0x01,0x01, 0x01,
		// 0x01, 0x05, 0x05, 0x03, 0x06, 0x06 };

		// 清一色第三坎牌
		// int cards[] = new int[] { 0x02, 0x02, 0x07, 0x07, 0x07,0x01, 0x04,
		// 0x04, 0x05, 0x05, 0x03, 0x06, 0x06 };

		// 清一色 豪华七小对
		// int cards[] = new int[] { 0x07, 0x07, 0x02, 0x02, 0x01,0x01, 0x01,
		// 0x01, 0x05, 0x05, 0x03, 0x06, 0x06 };

		// int cards[] = new int[] { 0x02, 0x02, 0x02, 0x02, 0x15, 0x15, 0x15,
		// 0x15, 0x22, 0x25, 0x25,
		//
		// 0x28, 0x28 };

		// int cards[] = new int[] { 0x01, 0x02, 0x03, 0x17, 0x17, 0x21, 0x22,
		// 0x22, 0x23, 0x24,
		//
		// 0x26, 0x28,0x35 };

		// int cards[] = new int[] { 0x21, 0x21, 0x03, 0x11, 0x12, 0x13, 0x11,
		// 0x12, 0x23, 0x24, 0x25,
		//
		// 0x28, 0x22 };
		// int cards[] = new int[] { 0x16, 0x16,0x16, 0x11, 0x11, 0x11, 0x02,
		// 0x03, 0x04, 0x05, 0x05, 0x29, 0x29 };

		// int cards[] = new int[] { 0x27, 0x27,0x27, 0x27, 0x01, 0x11, 0x21,
		// 0x23, 0x25, 0x05, 0x04, 0x08, 0x16 };

		// 豪华七小对，海底少牌
		// int cards[] = new int[] { 0x01, 0x01, 0x04, 0x04, 0x07, 0x07, 0x15,
		// 0x15, 0x15, 0x25, 0x25,0x19, 0x19 };
		// int cards[] = new int[] { 0x01, 0x01, 0x04, 0x04, 0x07, 0x07, 0x15,
		// 0x15, 0x15, 0x25, 0x25,0x19, 0x19 };
		// int cards[] = new int[] { 0x01, 0x01, 0x04, 0x04, 0x07, 0x07, 0x15,
		// 0x15, 0x15, 0x25, 0x25,0x19, 0x19 };
		// int cards[] = new int[] { 0x02, 0x02, 0x08, 0x12, 0x12, 0x15, 0x15,
		// 0x15, 0x15, 0x18, 0x22,0x25, 0x25 };
		// int cards[] = new int[] {
		// 0x11,0x11,0x11,0x19,0x13,0x13,0x14,0x14,0x13,0x13,0x16,0x16,0x16 };
		// 七小
		// int cards[] = new int[] { 0x11, 0x11, 0x12, 0x12, 0x12, 0x18, 0x16,
		// 0x16, 0x15, 0x15, 0x18,0x18, 0x12 };
		// int cards[] = new int[] { 0x11, 0x11, 0x12, 0x12, 0x13, 0x13, 0x16,
		// 0x16, 0x15, 0x15, 0x19,0x19, 0x18 };
		// int cards[] = new int[] { 0x11, 0x11, 0x12, 0x12, 0x12, 0x19, 0x16,
		// 0x16, 0x15, 0x15, 0x19,0x19, 0x19 };
		//
		// 多人抢杠胡
		// int cards[] = new int[]
		// {0x11,0x11,0x11,0x13,0x14,0x15,0x15,0x15,0x22,0x22,0x27,0x27,0x27};

		// int cards[] = new int[] { 0x11, 0x11, 0x12, 0x12, 0x12, 0x16, 0x16,
		// 0x16, 0x17, 0x19, 0x17,0x18, 0x19 };
		//
		// 天胡
		// int cards[] = new int[] { 0x11, 0x12, 0x13, 0x02, 0x04, 0x03, 0x15,
		// 0x15, 0x18, 0x18, 0x17,0x18, 0x19 };
		// 海底
		// int cards[] = new int[] { 0x11, 0x12, 0x13, 0x02, 0x04, 0x03, 0x15,
		// 0x15, 0x18, 0x18, 0x19,0x18, 0x19 };
		// 七小
		// int cards[] = new int[] { 0x35, 0x35, 0x35, 0x35, 0x04, 0x14, 0x15,
		// 0x25, 0x17, 0x27, 0x08,
		//
		// 0x28, 0x19 };
		//
		// int cards[] = new int[] { 0x01, 0x02, 0x02, 0x04, 0x06, 0x06, 0x17,
		// 0x18, 0x19, 0x22, 0x23,
		//
		// 0x35, 0x35 };

		// int cards[] = new int[]
		// {0x11,0x12,0x13,0x01,0x02,0x03,0x22,0x23,0x24,0x26,0x27,0x28,0x28};

		// int cards[] = new int[] { 0x01, 0x02, 0x03, 0x27, 0x28,0x29, 0x04,
		// 0x04, 0x19, 0x18, 0x17, 0x04, 0x21 };

		/*
		 * 小胡 1 、大四喜：起完牌后，玩家手上已有四张一样的牌，即可胡牌。（四喜计分等同小胡自摸） 2 、板板胡：起完牌后，玩家手上没有一张 2
		 * 、 5 、 8 （将牌），即可胡牌。（等同小胡自摸） 3 、缺一色：起完牌后，玩家手上筒、索、万任缺一门，即可胡牌。（等同小胡自摸） 4
		 * 、六六顺：起完牌后，玩家手上已有 2 个刻子（刻子：三个一样的牌），即可胡牌。（等同小胡自摸） 5 、平胡： 2 、 5 、 8
		 * 作将，其余成刻子或顺子或一句话，即可胡牌。
		 */
		// int cards[] = new int[]{
		// 0x01,
		// 0x01,
		// 0x01,
		// 0x13,
		// 0x13,
		// 0x13,
		// 0x13,
		// 0x16,
		// 0x16,
		// 0x16,
		// 0x17,
		//
		// 0x17,
		// 0x29
		// };

		// 4个人牌不一样 ---打开下面的代码 配置牌型即可
		// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
		// for (int j = 0; j < MJGameConstants.MAX_INDEX; j++) {
		// GRR._cards_index[i][j] = 0;
		// }
		// }
		// int _cards0[] = new int[]
		// {0x01,0x02,0x04,0x04,0x05,0x07,0x07,0x08,0x14,0x19,0x21,0x23,0x29};
		// int _cards1[] = new int[]
		// {0x01,0x06,0x08,0x08,0x11,0x12,0x13,0x16,0x18,0x21,0x25,0x27,0x29};
		// int _cards2[] = new int[]
		// {0x02,0x03,0x05,0x07,0x09,0x12,0x14,0x18,0x24,0x21,0x25,0x25,0x27};
		// int _cards3[] = new int[]
		// {0x01,0x02,0x04,0x04,0x05,0x08,0x17,0x18,0x24,0x24,0x22,0x28,0x28};
		// for (int j = 0; j < 13; j++) {
		// GRR._cards_index[0][_logic.switch_to_card_index(_cards0[j])] += 1;
		// GRR._cards_index[1][_logic.switch_to_card_index(_cards1[j])] += 1;
		// GRR._cards_index[2][_logic.switch_to_card_index(_cards2[j])] += 1;
		// GRR._cards_index[3][_logic.switch_to_card_index(_cards3[j])] += 1;
		// }

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < GameConstants.MAX_INDEX; j++) {
				GRR._cards_index[i][j] = 0;
			}
		}
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < 13; j++) {
				GRR._cards_index[i][_logic.switch_to_card_index(cards[j])] += 1;
			}
		}

		/*************** 如果要测试线上实际牌 把下面代码取消注释 放入实际牌型即可 ***********************/
		// int[] realyCards = new int[] {8, 22, 24, 18, 3, 8, 36, 1, 23, 25, 24,
		// 34, 37, 23, 41, 34, 23, 20, 19, 25, 25, 9, 1, 2, 3, 39, 4, 1, 5, 17,
		// 53, 2, 37, 23, 18, 8, 21, 6, 38, 3, 33, 53, 33, 36, 22, 53, 36, 4,
		// 22, 33, 21, 35, 38, 9, 19, 38, 20, 40, 21, 6, 38, 24, 24, 3, 53, 17,
		// 35, 33, 37, 18, 17, 34, 35, 20, 8, 9, 36, 5, 41, 20, 7, 19, 4, 5, 40,
		// 1, 34, 18, 37, 35, 40, 41, 22, 39, 2, 21, 19, 6, 41, 7, 5, 9, 7, 4,
		// 7, 2, 39, 40, 39, 25, 6, 17};
		// testRealyCard(realyCards);

		/********
		 * 下面这个给测试用的 如果开发用 把上面的注释去掉 realyCards
		 **************************/

		if (BACK_DEBUG_CARDS_MODE) {
			if (debug_my_cards != null) {
				if (debug_my_cards.length > 14) {
					int[] temps = new int[debug_my_cards.length];
					System.arraycopy(debug_my_cards, 0, temps, 0, temps.length);
					testRealyCard(temps);
					debug_my_cards = null;
				} else {
					int[] temps = new int[debug_my_cards.length];
					System.arraycopy(debug_my_cards, 0, temps, 0, temps.length);
					testSameCard(temps);
					debug_my_cards = null;
				}
			}

		}
	}

	/**
	 * 测试线上 实际牌
	 */
	public void testRealyCard(int[] realyCards) {
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < GameConstants.MAX_INDEX; j++) {
				GRR._cards_index[i][j] = 0;
			}
		}
		this._repertory_card = realyCards;
		_all_card_len = _repertory_card.length;
		GRR._left_card_count = _all_card_len;

		int send_count;
		int have_send_count = 0;

		// 分发扑克
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			send_count = (GameConstants.MAX_COUNT - 1);
			GRR._left_card_count -= send_count;

			// 一人13张牌,庄家多一张
			_logic.switch_to_cards_index(_repertory_card, have_send_count, send_count, GRR._cards_index[i]);

			have_send_count += send_count;
		}
		DEBUG_CARDS_MODE = false;// 把调试模式关闭
		BACK_DEBUG_CARDS_MODE = false;
		System.err.println("=========开始调试线上牌型 调试模式自动关闭*=========");

	}

	/**
	 * 模拟牌型--相同牌
	 */
	public void testSameCard(int[] cards) {
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < GameConstants.MAX_INDEX; j++) {
				GRR._cards_index[i][j] = 0;
			}
		}
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < 13; j++) {
				GRR._cards_index[i][_logic.switch_to_card_index(cards[j])] += 1;
			}
		}
		DEBUG_CARDS_MODE = false;// 把调试模式关闭
		BACK_DEBUG_CARDS_MODE = false;
	}

	// 开始转转麻将
	private boolean game_start_zz() {
		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}

		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);
			}

			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			roomResponse.setGameStart(gameStartResponse);
			roomResponse
					.setCurrentPlayer(_current_player == GameConstants.INVALID_SEAT ? _resume_player : _current_player);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			roomResponse.setGameStatus(_game_status);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			this.send_response_to_player(i, roomResponse);
		}
		////////////////////////////////////////////////// 回放
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}

		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// _table_scheduled = GameSchedule.put(new
		// DispatchCardRunnable(this.getRoom_id(), _current_player, false),
		// MJGameConstants.SEND_CARD_DELAY, TimeUnit.MILLISECONDS);

		this.exe_dispatch_card(_current_player, GameConstants.WIK_NULL, GameConstants.DELAY_SEND_CARD_DELAY);

		return false;
	}

	// 开始转转麻将
	private boolean game_start_hz() {
		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}

		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);
			}

			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			roomResponse.setGameStart(gameStartResponse);
			roomResponse
					.setCurrentPlayer(_current_player == GameConstants.INVALID_SEAT ? _resume_player : _current_player);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			roomResponse.setGameStatus(_game_status);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			this.send_response_to_player(i, roomResponse);
		}
		////////////////////////////////////////////////// 回放
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}

		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// 检测听牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_playerStatus[i]._hu_card_count = this.get_hz_ting_card(_playerStatus[i]._hu_cards, GRR._cards_index[i],
					GRR._weave_items[i], GRR._weave_count[i], false);
			if (_playerStatus[i]._hu_card_count > 0) {
				this.operate_chi_hu_cards(i, _playerStatus[i]._hu_card_count, _playerStatus[i]._hu_cards);
			}
		}
		boolean is_qishou_hu = false;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 起手4个红中
			if (GRR._cards_index[i][_logic.get_magic_card_index(0)] == 4) {

				_playerStatus[i].add_action(GameConstants.WIK_ZI_MO);
				_playerStatus[i].add_zi_mo(_logic.switch_to_card_data(_logic.get_magic_card_index(0)), i);
				GRR._chi_hu_rights[i].opr_or(GameConstants.CHR_ZI_MO);
				GRR._chi_hu_rights[i].opr_or(GameConstants.CHR_HUNAN_HZ_QISHOU_HU);
				this.exe_qishou_hongzhong(i);

				is_qishou_hu = true;
				break;
			}
		}
		if (is_qishou_hu == false) {
			this.exe_dispatch_card(_current_player, GameConstants.WIK_NULL, GameConstants.DELAY_SEND_CARD_DELAY);
		}
		// this.exe_dispatch_card(_current_player,true);

		return false;
	}

	// 开始河南红中麻将
	private boolean game_start_hnhz() {
		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}

		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);
			}

			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			roomResponse.setGameStart(gameStartResponse);
			roomResponse
					.setCurrentPlayer(_current_player == GameConstants.INVALID_SEAT ? _resume_player : _current_player);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			roomResponse.setGameStatus(_game_status);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			this.send_response_to_player(i, roomResponse);
		}
		////////////////////////////////////////////////// 回放
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}

		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// 检测听牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_playerStatus[i]._hu_card_count = this.get_hz_ting_card(_playerStatus[i]._hu_cards, GRR._cards_index[i],
					GRR._weave_items[i], GRR._weave_count[i], false);
			if (_playerStatus[i]._hu_card_count > 0) {
				this.operate_chi_hu_cards(i, _playerStatus[i]._hu_card_count, _playerStatus[i]._hu_cards);
			}
		}
		boolean is_qishou_hu = false;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 起手4个红中
			if (GRR._cards_index[i][_logic.get_magic_card_index(0)] == 4) {

				_playerStatus[i].add_action(GameConstants.WIK_ZI_MO);
				_playerStatus[i].add_zi_mo(_logic.switch_to_card_data(_logic.get_magic_card_index(0)), i);
				GRR._chi_hu_rights[i].opr_or(GameConstants.CHR_ZI_MO);
				GRR._chi_hu_rights[i].opr_or(GameConstants.CHR_HENAN_HZ_QISHOU_HU);
				this.exe_qishou_hongzhong(i);

				is_qishou_hu = true;
				break;
			}
		}
		if (is_qishou_hu == false) {
			this.exe_dispatch_card(_current_player, GameConstants.WIK_NULL, GameConstants.DELAY_SEND_CARD_DELAY);
		}
		// this.exe_dispatch_card(_current_player,true);

		return false;
	}

	// 开始转转麻将
	private boolean game_start_sg() {
		_logic.clean_magic_cards();

		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}

		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);
			}

			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			roomResponse.setGameStart(gameStartResponse);
			roomResponse
					.setCurrentPlayer(_current_player == GameConstants.INVALID_SEAT ? _resume_player : _current_player);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			roomResponse.setGameStatus(_game_status);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			this.send_response_to_player(i, roomResponse);
		}
		////////////////////////////////////////////////// 回放
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}

		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 延迟调度定位牌
		GameSchedule.put(new DingGuiRunnable(this.getRoom_id(), GRR._banker_player), 1200, TimeUnit.MILLISECONDS);

		return false;
	}

	// 开始株洲麻将
	private boolean game_start_zhuzhou() {

		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);

				cards.addItem(hand_cards[i][j]);
			}
			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);

			roomResponse.setGameStart(gameStartResponse);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			this.send_response_to_player(i, roomResponse);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}

		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);
		//this.exe_tian_hu(_current_player);
		
		this.exe_dispatch_card(_current_player,GameConstants.DispatchCard_Type_Tian_Hu,0);
		return true;
	}

	/**
	 * // 开始仙桃晃晃麻将
	 * 
	 * @return
	 */
	private boolean game_start_xthh() {
		_logic.clean_magic_cards();

		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}

		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);
			}

			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			roomResponse.setGameStart(gameStartResponse);
			roomResponse
					.setCurrentPlayer(_current_player == GameConstants.INVALID_SEAT ? _resume_player : _current_player);
			roomResponse.setLeftCardCount(GRR._left_card_count);
			roomResponse.setGameStatus(_game_status);
			this.send_response_to_player(i, roomResponse);
		}
		////////////////////////////////////////////////// 回放
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}
		// GRR._left_card_count=6;
		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);

		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 延迟调度定位牌
		GameSchedule.put(new StartLaiGenRunnable(this.getRoom_id(), GRR._banker_player), 1200, TimeUnit.MILLISECONDS);

		return false;
	}

	/**
	 * 开始 安阳麻将
	 * 
	 * @return
	 */
	private boolean game_start_ay() {

		GRR._banker_player = _current_player = GameConstants.INVALID_SEAT;

		this._handler = this._handler_pao_qiang;
		this._handler_pao_qiang.exe(this);

		return false;
	}

	/**
	 * 开始 林州麻将
	 * 
	 * @return
	 */
	private boolean game_start_lz() {

		GRR._banker_player = _current_player = GameConstants.INVALID_SEAT;

		this._handler = this._handler_pao;
		this._handler_pao.exe(this);

		return false;
	}

	/**
	 * 开始 河南麻将
	 * 
	 * @return
	 */
	private boolean game_start_henan() {
		_logic.clean_magic_cards();

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_PAO)) {
			this._handler = this._handler_pao_henna;
			this._handler_pao_henna.exe(this);
			return true;
		} else {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_player_result.pao[i] = 0;// 清掉 默认是-1
			}
		}

		this.GRR._banker_player = this._current_player = this._banker_select;
		// 游戏开始
		this._game_status = GameConstants.GS_MJ_PLAY;// 设置状态
		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(this.GRR._banker_player);
		gameStartResponse.setCurrentPlayer(this._current_player);
		gameStartResponse.setLeftCardCount(this.GRR._left_card_count);

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = this._logic.switch_to_cards_data(this.GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}

		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);
			}

			// 回放数据
			this.GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);

			if (this._cur_round == 1) {
				// shuffle_players();
				this.load_player_info_data(roomResponse);
			}
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			roomResponse.setGameStart(gameStartResponse);
			roomResponse.setCurrentPlayer(
					this._current_player == GameConstants.INVALID_SEAT ? this._resume_player : this._current_player);
			roomResponse.setLeftCardCount(this.GRR._left_card_count);
			roomResponse.setGameStatus(this._game_status);
			roomResponse.setLeftCardCount(this.GRR._left_card_count);
			this.send_response_to_player(i, roomResponse);
		}
		////////////////////////////////////////////////// 回放
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);
		this.load_player_info_data(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}

		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(this.GRR._left_card_count);
		this.GRR.add_room_response(roomResponse);

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {
			exe_hun(this.GRR._banker_player);
			return true;
		}

		// 检测听牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			this._playerStatus[i]._hu_card_count = this.get_henan_ting_card(this._playerStatus[i]._hu_cards,
					this.GRR._cards_index[i], this.GRR._weave_items[i], this.GRR._weave_count[i]);
			if (this._playerStatus[i]._hu_card_count > 0) {
				this.operate_chi_hu_cards(i, this._playerStatus[i]._hu_card_count, this._playerStatus[i]._hu_cards);
			}
		}

		this.exe_dispatch_card(_current_player, GameConstants.WIK_NULL, 0);

		return true;
	}

	// 开始长沙麻将 有小胡
	private boolean game_start_cs() {
		GameStartResponse.Builder gameStartResponse = GameStartResponse.newBuilder();
		// gameStartResponse.setSiceIndex(rand);
		gameStartResponse.setBankerPlayer(GRR._banker_player);
		gameStartResponse.setCurrentPlayer(_current_player);
		gameStartResponse.setLeftCardCount(GRR._left_card_count);

		// 判断是否有小胡

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			PlayerStatus playerStatus = _playerStatus[i];
			// 胡牌判断
			int action = this.analyse_chi_hu_card_cs_xiaohu(GRR._cards_index[i], GRR._start_hu_right[i]);

			// 小胡
			if (action != GameConstants.WIK_NULL) {
				playerStatus.add_action(GameConstants.WIK_XIAO_HU);
				_game_status = GameConstants.GS_MJ_XIAOHU;// 设置状态
			} else {
				GRR._start_hu_right[i].set_empty();
			}
		}

		int hand_cards[][] = new int[GameConstants.GAME_PLAYER][GameConstants.MAX_COUNT];
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], hand_cards[i]);
			gameStartResponse.addCardsCount(hand_card_count);
		}
		// 发送数据
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			// 只发自己的牌
			gameStartResponse.clearCardData();
			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				gameStartResponse.addCardData(hand_cards[i][j]);

				cards.addItem(hand_cards[i][j]);
			}
			// 回放数据
			GRR._video_recode.addHandCards(cards);

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
			this.load_room_info_data(roomResponse);
			this.load_common_status(roomResponse);

			if (this._cur_round == 1) {
				// shuffle_players();
				this.load_player_info_data(roomResponse);
			}

			roomResponse.setGameStart(gameStartResponse);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			this.send_response_to_player(i, roomResponse);
		}

		////////////////////////////////////////////////////////////////////////////////////////////////
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_START);
		this.load_room_info_data(roomResponse);
		this.load_common_status(roomResponse);

		if (this._cur_round == 1) {
			// shuffle_players();
			this.load_player_info_data(roomResponse);
		}
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			Int32ArrayResponse.Builder cards = Int32ArrayResponse.newBuilder();

			for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
				cards.addItem(hand_cards[i][j]);
			}
			gameStartResponse.addCardsData(cards);
		}
		roomResponse.setGameStart(gameStartResponse);
		roomResponse.setLeftCardCount(GRR._left_card_count);
		GRR.add_room_response(roomResponse);

		// GRR._left_card_count=1;
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// 有小胡
		if (_game_status == GameConstants.GS_MJ_XIAOHU) {
			//
			// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
			// PlayerStatus playerStatus = _playerStatus[i];
			// if(playerStatus._action_count>0){
			// this.operate_player_action(i, false);
			// }
			// }
			this.exe_xiao_hu(_current_player);
		} else {
			this.exe_dispatch_card(_current_player, GameConstants.WIK_NULL, 0);
			// _table_scheduled = GameSchedule.put(new
			// DispatchCardRunnable(this.getRoom_id(), _current_player, false),
			// MJGameConstants.SEND_CARD_DELAY, TimeUnit.MILLISECONDS);
		}

		return _game_status == GameConstants.GS_MJ_XIAOHU;
	}

	// 长沙麻将 杠牌,发两张牌,大家都可以看
	// public boolean dispath_cs_gang_card_data(int cur_player){
	// // 状态效验
	// if (cur_player == MJGameConstants.INVALID_SEAT)
	// return false;
	//
	// _status_cs_gang = true;
	//
	// _playerStatus[cur_player].set_card_status(MJGameConstants.CARD_STATUS_CS_GANG);
	// _playerStatus[cur_player].chi_hu_round_valid();//可以胡了
	//
	//
	// this._gang_card_data.clean_cards();
	//
	// PlayerStatus curPlayerStatus = _playerStatus[cur_player];
	// curPlayerStatus.reset();
	//
	// // 设置变量
	// _out_card_data = MJGameConstants.INVALID_VALUE;
	// _out_card_player = MJGameConstants.INVALID_SEAT;
	// _current_player = cur_player;// 轮到操作的人是自己
	//
	// _provide_player = cur_player;
	//
	//
	// int bu_card;
	// // 出牌响应判断
	//
	// // 从牌堆拿出2张牌
	// for(int i=0; i < MJGameConstants.CS_GANG_DRAW_COUNT; i++){
	// _send_card_count++;
	// if (is_mj_type(MJGameConstants.GAME_TYPE_HZ)) {
	// bu_card = _repertory_card_zz[_all_card_len-GRR._left_card_count];
	// } else {
	// bu_card = _repertory_card_cs[_all_card_len-GRR._left_card_count];
	// }
	// --GRR._left_card_count;
	// this._gang_card_data.add_card(bu_card);
	//
	// }
	//
	// //显示两张牌
	// this.operate_out_card(cur_player, MJGameConstants.CS_GANG_DRAW_COUNT,
	// this._gang_card_data.get_cards(),MJGameConstants.OUT_CARD_TYPE_MID,MJGameConstants.INVALID_SEAT);
	//
	// boolean has_action =false;
	//// for(int i=0; i<MJGameConstants.CS_GANG_DRAW_COUNT;i++){
	//// ChiHuRight chr = GRR._chi_hu_rights[i];
	//// chr.set_empty();
	//// }
	// //显示玩家对这两张牌的操作
	// for(int i=0; i < MJGameConstants.CS_GANG_DRAW_COUNT; i++){
	// boolean bAroseAction = false;
	// bu_card = this._gang_card_data.get_card(i);
	//
	// for(int k=0; k < MJGameConstants.GAME_PLAYER;k++){
	// //自己只有杠和胡
	// if(k==cur_player){
	// ChiHuRight chr = GRR._chi_hu_rights[k];
	// chr.set_empty();
	//
	// int action = analyse_chi_hu_card(GRR._cards_index[cur_player],
	// GRR._weave_items[cur_player], GRR._weave_count[cur_player], bu_card,
	// chr,true);//自摸
	// if(action != MJGameConstants.WIK_NULL){
	// //添加动作
	// curPlayerStatus.add_action(MJGameConstants.WIK_ZI_MO);
	// curPlayerStatus.add_zi_mo(bu_card,cur_player);
	// bAroseAction=true;
	// }
	// // 加到手牌
	// GRR._cards_index[cur_player][_logic.switch_to_card_index(bu_card)]++;
	//
	// // 如果牌堆还有牌，判断能不能杠
	// if (GRR._left_card_count > 0) {
	// GangCardResult gangCardResult = new GangCardResult();
	// int cbActionMask=_logic.analyse_gang_card(GRR._cards_index[cur_player],
	// GRR._weave_items[cur_player], GRR._weave_count[cur_player],
	// gangCardResult,true);
	//
	// if(cbActionMask!=MJGameConstants.WIK_NULL){//有杠
	// curPlayerStatus.add_action(MJGameConstants.WIK_GANG);//听牌的时候可以杠
	// for(int j= 0; j < gangCardResult.cbCardCount; j++){
	// curPlayerStatus.add_gang(gangCardResult.cbCardData[j], cur_player,
	// gangCardResult.isPublic[j]);
	// }
	//
	// bAroseAction=true;
	// }
	// }
	// GRR._cards_index[cur_player][_logic.switch_to_card_index(bu_card)]--;
	// }else{
	//
	// bAroseAction = this.estimate_cs_gang_respond(k, bu_card,false);//,
	// EstimatKind.EstimatKind_OutCard
	// }
	// // 出牌响应判断
	//
	// // 如果没有需要操作的玩家，派发扑克
	// if (bAroseAction == true) {
	// has_action= true;
	//
	// }
	// }
	// }
	//
	//
	// if(has_action==false){
	// _status_cs_gang = false;
	// //添加到牌堆
	//// for(int i=0; i < this._gang_card_data.get_card_count(); i++){
	//// GRR._discard_count[cur_player]++;
	//// GRR._discard_cards[cur_player][GRR._discard_count[cur_player] - 1] =
	// this._gang_card_data.get_card(i);
	//// }
	////
	//// this.operate_add_discard(cur_player,
	// this._gang_card_data.get_card_count(),
	// this._gang_card_data.get_card_count());
	//
	// _provide_player = MJGameConstants.INVALID_SEAT;
	// _out_card_player = cur_player;
	//
	// _table_scheduled = GameSchedule.put(new
	// AddDiscardRunnable(this.getRoom_id(), cur_player,
	// this._gang_card_data.get_card_count(),this._gang_card_data.get_cards()),
	// MJGameConstants.SEND_CARD_DELAY-100, TimeUnit.MILLISECONDS);
	//
	//
	// //继续发牌
	// _current_player = (cur_player+1)%MJGameConstants.GAME_PLAYER;
	//
	// _table_scheduled = GameSchedule.put(new
	// DispatchCardRunnable(this.getRoom_id(), _current_player, false),
	// MJGameConstants.SEND_CARD_DELAY, TimeUnit.MILLISECONDS);
	//
	// return false;
	//
	// }else{
	// _provide_player = cur_player;
	// //玩家有操作
	// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	// if(_playerStatus[i].has_action()){
	// this.operate_player_action(i, false);
	// }
	// }
	// return true;
	// }
	// }

	// 摸起来的牌 派发牌
	public boolean dispatch_card_data(int cur_player, int type, boolean tail) {

		// 发牌
		this._handler = this._handler_dispath_card;
		this._handler_dispath_card.reset_status(cur_player, type);
		this._handler.exe(this);

		return true;
	}

	// 游戏结束
	public boolean handler_game_finish(int seat_index, int reason) {
		_game_status = GameConstants.GS_MJ_WAIT;

		if (is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
			return this.handler_game_finish_sg(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {
			return this.handler_game_finish_xthh(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			return this.handler_game_finish_ay(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)) {
			return this.handler_game_finish_lz(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_CS)) {
			return this.handler_game_finish_cs(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			return this.handler_game_finish_henan(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			return this.handler_game_finish_henan(seat_index, reason);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
			return this.handler_game_finish_henan_hz(seat_index, reason);
		}

		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);
		game_end.setEndTime(System.currentTimeMillis() / 1000L);// 结束时间
		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);

			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i]);

			}

			GRR._end_type = reason;

			// 重新算小胡分数
			if (this.is_mj_type(GameConstants.GAME_TYPE_CS)) {

				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					if (GRR._start_hu_right[p].is_valid()) {
						int lStartHuScore = 0;

						int wFanShu = _logic.get_chi_hu_action_rank_cs(GRR._start_hu_right[p]);

						lStartHuScore = wFanShu * GameConstants.CELL_SCORE;

						for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
							if (i == p)
								continue;
							int s = lStartHuScore;
							// 庄闲
							if (is_zhuang_xian()) {
								if ((GRR._banker_player == p) || (GRR._banker_player == i)) {
									s += s;
								}
							}
							int niao = GRR._player_niao_count[p] + GRR._player_niao_count[i];
							if (niao > 0) {
								// for(int h =0; h < niao; h++){
								// s*=2;
								// }
								s *= (niao + 1);
							}
							GRR._start_hu_score[i] -= s;// 输的番薯
							GRR._start_hu_score[p] += s;
						}
					}
				}

			}
			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

				for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
					for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
						lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
						// _allGRR._gang_score[i].scores[j][k]+=
						// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

						// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
					}
				}

				// 记录
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
				}

			}

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._game_score[i] += lGangScore[i];
				GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

				// 记录
				// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
				_player_result.game_score[i] += GRR._game_score[i];

			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);
			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {

					cs.addItem(GRR._cards_data[i][j]);
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	/**
	 * 仙桃晃晃结束
	 * 
	 * @param seat_index
	 * @param reason
	 * @return
	 */
	public boolean handler_game_finish_xthh(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);

		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);
			game_end.setGameTypeIndex(GRR._game_type_index);
			if (GRR._especial_txt != "") {
				game_end.setEspecialTxt(GRR._especial_txt);
				game_end.setEspecialTxtType(GRR._especial_txt_type);
			}

			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			// for (int i = 0; i < GRR._especial_card_count; i++) {
			// game_end.addEspecialShowCards(GRR._especial_show_cards[i]+MJGameConstants.CARD_ESPECIAL_TYPE_LAI_GEN);
			// }
			game_end.addEspecialShowCards(GRR._especial_show_cards[0] + GameConstants.CARD_ESPECIAL_TYPE_LAI_GEN);
			game_end.addEspecialShowCards(GRR._especial_show_cards[1]);

			GRR._end_type = reason;

			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

				for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
					for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
						lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
						// _allGRR._gang_score[i].scores[j][k]+=
						// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

						// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
					}
				}

				// 记录
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
				}

			}
			//
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_player_result.game_score[i] += GRR._game_score[i];

				// 这两个分数已经加过了
				GRR._game_score[i] += lGangScore[i];
				GRR._game_score[i] += GRR._piao_lai_score[i];
			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);
			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {
					// 癞子
					if (_logic.is_magic_card(GRR._cards_data[i][j])) {
						cs.addItem(GRR._cards_data[i][j] + GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI);
					} else {
						cs.addItem(GRR._cards_data[i][j]);
					}
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	public boolean handler_game_finish_cs(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);
		game_end.setEndTime(System.currentTimeMillis() / 1000L);// 结束时间
		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);

			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i]);

			}

			GRR._end_type = reason;

			// 重新算小胡分数
			if (this.is_mj_type(GameConstants.GAME_TYPE_CS)) {

				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					if (GRR._start_hu_right[p].is_valid()) {
						int lStartHuScore = 0;

						int wFanShu = _logic.get_chi_hu_action_rank_cs(GRR._start_hu_right[p]);

						lStartHuScore = wFanShu * GameConstants.CELL_SCORE;

						for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
							if (i == p)
								continue;
							int s = lStartHuScore;
							// 庄闲
							if (is_zhuang_xian()) {
								if ((GRR._banker_player == p) || (GRR._banker_player == i)) {
									s += s;
								}
							}
							int niao = GRR._player_niao_count[p] + GRR._player_niao_count[i];
							if (niao > 0) {
								// for(int h =0; h < niao; h++){
								// s*=2;
								// }
								s *= (niao + 1);
							}
							GRR._start_hu_score[i] -= s;// 输的番薯
							GRR._start_hu_score[p] += s;
						}
					}
				}

			}
			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

				for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
					for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
						lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
						// _allGRR._gang_score[i].scores[j][k]+=
						// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

						// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
					}
				}

				// 记录
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
				}

			}

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._game_score[i] += lGangScore[i];
				GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

				// 记录
				// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
				_player_result.game_score[i] += GRR._game_score[i];

			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);
			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {

					cs.addItem(GRR._cards_data[i][j]);
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					if (j < GRR._weave_count[i]) {
						weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					} else {
						weaveItem_item.setWeaveKind(GameConstants.WIK_NULL);
					}

					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	public boolean handler_game_finish_sg(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);

		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);
			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i] + GameConstants.CARD_ESPECIAL_TYPE_DING_GUI);
			}

			GRR._end_type = reason;

			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

				for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
					for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
						lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
						// _allGRR._gang_score[i].scores[j][k]+=
						// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

						// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
					}
				}

				// 记录
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
				}

			}

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._game_score[i] += lGangScore[i];
				GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

				// 记录
				// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
				_player_result.game_score[i] += GRR._game_score[i];

			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);
			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {
					// 癞子
					if (_logic.is_magic_card(GRR._cards_data[i][j])) {
						cs.addItem(GRR._cards_data[i][j] + GameConstants.CARD_ESPECIAL_TYPE_GUI);
					} else {
						cs.addItem(GRR._cards_data[i][j]);
					}
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	public boolean handler_game_finish_ay(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			game_end.addPao(_player_result.pao[i]);
			game_end.addQiang(_player_result.qiang[i]);
		}

		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);
			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i] + GameConstants.CARD_ESPECIAL_TYPE_DING_GUI);
			}

			GRR._end_type = reason;
			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			if (reason == GameConstants.Game_End_NORMAL) {
				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

					for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
						for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
							lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
							// _allGRR._gang_score[i].scores[j][k]+=
							// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

							// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
						}
					}

					// 记录
					for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
						_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
					}

				}

				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					GRR._game_score[i] += lGangScore[i];
					GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

					// 记录
					// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
					_player_result.game_score[i] += GRR._game_score[i];

				}
			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);

				if (GRR._chi_hu_rights[i].bao_ting_card != 0) {
					game_end.addBaoTingCards(
							GRR._chi_hu_rights[i].bao_ting_card + GameConstants.CARD_ESPECIAL_TYPE_BAO_TING);// 报听的牌
				} else {
					game_end.addBaoTingCards(0);// 报听的牌
				}

			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {
					// 癞子
					if (_logic.is_magic_card(GRR._cards_data[i][j])) {
						cs.addItem(GRR._cards_data[i][j] + GameConstants.CARD_ESPECIAL_TYPE_GUI);
					} else {
						cs.addItem(GRR._cards_data[i][j]);
					}
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	public boolean handler_game_finish_lz(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			game_end.addPao(_player_result.pao[i]);
		}

		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);
			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i] + GameConstants.CARD_ESPECIAL_TYPE_DING_GUI);
			}

			GRR._end_type = reason;
			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			if (reason == GameConstants.Game_End_NORMAL) {
				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

					for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
						for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
							lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
							// _allGRR._gang_score[i].scores[j][k]+=
							// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

							// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
						}
					}

					// 记录
					for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
						_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
					}

				}

				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					GRR._game_score[i] += lGangScore[i];
					GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

					// 记录
					// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
					_player_result.game_score[i] += GRR._game_score[i];

				}
			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);

				if (GRR._chi_hu_rights[i].bao_ting_card != 0) {
					game_end.addBaoTingCards(
							GRR._chi_hu_rights[i].bao_ting_card + GameConstants.CARD_ESPECIAL_TYPE_BAO_TING);// 报听的牌
				} else {
					game_end.addBaoTingCards(0);// 报听的牌
				}

			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {
					// 癞子
					if (_logic.is_magic_card(GRR._cards_data[i][j])) {
						cs.addItem(GRR._cards_data[i][j] + GameConstants.CARD_ESPECIAL_TYPE_GUI);
					} else {
						cs.addItem(GRR._cards_data[i][j]);
					}
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	/**
	 * 
	 * @param seat_index
	 * @param reason
	 * @return
	 */
	public boolean handler_game_finish_henan_hz(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);
		game_end.setEndTime(System.currentTimeMillis() / 1000L);// 结束时间
		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);

			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i]);

			}

			GRR._end_type = reason;

			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

				for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
					for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
						lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
						// _allGRR._gang_score[i].scores[j][k]+=
						// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

						// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
					}
				}

				// 记录
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
				}

			}

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._game_score[i] += lGangScore[i];
				GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

				// 记录
				// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
				_player_result.game_score[i] += GRR._game_score[i];

			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// 设置中鸟数据
			for (int i = 0; i < GameConstants.MAX_NIAO_CARD && i < GRR._count_niao; i++) {
				game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			}
			game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);
			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {

					cs.addItem(GRR._cards_data[i][j]);
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	public boolean handler_game_finish_henan(int seat_index, int reason) {
		int real_reason = reason;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_ready[i] = 0;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_END);
		GameEndResponse.Builder game_end = GameEndResponse.newBuilder();

		roomResponse.setLeftCardCount(0);

		this.load_common_status(roomResponse);
		this.load_room_info_data(roomResponse);

		// 这里记录了两次，先这样
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		game_end.setRoomInfo(room_info);

		game_end.setRoundOverType(0);
		game_end.setRoomOverType(0);

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			game_end.addPao(_player_result.pao[i]);
		}

		if (GRR != null) {// reason == MJGameConstants.Game_End_NORMAL || reason
							// == MJGameConstants.Game_End_DRAW ||
			// (reason ==MJGameConstants.Game_End_RELEASE_PLAY && GRR!=null)
			game_end.setRoundOverType(1);
			game_end.setStartTime(GRR._start_time);
			game_end.setGameTypeIndex(GRR._game_type_index);
			roomResponse.setLeftCardCount(GRR._left_card_count);

			// 特别显示的牌
			for (int i = 0; i < GRR._especial_card_count; i++) {
				game_end.addEspecialShowCards(GRR._especial_show_cards[i] + GameConstants.CARD_ESPECIAL_TYPE_HUN);
			}

			GRR._end_type = reason;
			// 杠牌，每个人的分数
			float lGangScore[] = new float[GameConstants.GAME_PLAYER];
			if (reason == GameConstants.Game_End_NORMAL) {
				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

					for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
						for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
							lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
							// _allGRR._gang_score[i].scores[j][k]+=
							// GRR._gang_score[i].scores[j][k];//杠牌，每个人的分数

							// allGangScore[k]+=_allGRR._gang_score[i].scores[j][k];
						}
					}

					// 记录
					for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
						_player_result.lost_fan_shu[i][j] += GRR._lost_fan_shu[i][j];
					}

				}

				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					GRR._game_score[i] += lGangScore[i];
					GRR._game_score[i] += GRR._start_hu_score[i];// 起手胡分数

					// 记录
					// _all_start_hu_score[i]+=_start_hu_score[i];//起手胡分数
					_player_result.game_score[i] += GRR._game_score[i];

				}
			}

			this.load_player_info_data(roomResponse);

			game_end.setGameRound(_game_round);
			game_end.setCurRound(_cur_round);

			game_end.setCellScore(GameConstants.CELL_SCORE);

			game_end.setBankerPlayer(GRR._banker_player);// 专家
			game_end.setLeftCardCount(GRR._left_card_count);// 剩余牌
			game_end.setShowBirdEffect(GRR._show_bird_effect == false ? 0 : 1);

			// // 设置中鸟数据
			// for (int i = 0; i < MJGameConstants.MAX_NIAO_CARD && i <
			// GRR._count_niao; i++) {
			// game_end.addCardsDataNiao(GRR._cards_data_niao[i]);
			// }
			// game_end.setCountPickNiao(GRR._count_pick_niao);// 中鸟个数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				Int32ArrayResponse.Builder pnc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._player_niao_count[i]; j++) {
					pnc.addItem(GRR._player_niao_cards[i][j]);
				}
				game_end.addPlayerNiaoCards(pnc);
				game_end.addHuResult(GRR._hu_result[i]);

				// 胡的牌
				Int32ArrayResponse.Builder hc = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_COUNT; j++) {
					hc.addItem(GRR._chi_hu_card[i][j]);
				}

				game_end.addHuCardData(GRR._chi_hu_card[i][0]);
				game_end.addHuCardArray(hc);

				if (GRR._chi_hu_rights[i].bao_ting_card != 0) {
					game_end.addBaoTingCards(
							GRR._chi_hu_rights[i].bao_ting_card + GameConstants.CARD_ESPECIAL_TYPE_BAO_TING);// 报听的牌
				} else {
					game_end.addBaoTingCards(0);// 报听的牌
				}

			}

			// 现在权值只有一位
			long rv[] = new long[GameConstants.MAX_RIGHT_COUNT];

			// 设置胡牌描述
			this.set_result_describe();

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				GRR._card_count[i] = _logic.switch_to_cards_data(GRR._cards_index[i], GRR._cards_data[i]);

				Int32ArrayResponse.Builder cs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GRR._card_count[i]; j++) {
					// 癞子
					if (_logic.is_magic_card(GRR._cards_data[i][j])) {
						cs.addItem(GRR._cards_data[i][j] + GameConstants.CARD_ESPECIAL_TYPE_HUN);
					} else {
						cs.addItem(GRR._cards_data[i][j]);
					}
				}
				game_end.addCardsData(cs);// 牌

				// 组合
				WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
					WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
					weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
					weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
					weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
					weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
					weaveItem_array.addWeaveItem(weaveItem_item);
				}
				game_end.addWeaveItemArray(weaveItem_array);

				GRR._chi_hu_rights[i].get_right_data(rv);// 获取权位数值
				game_end.addChiHuRight(rv[0]);

				GRR._start_hu_right[i].get_right_data(rv);// 获取权位数值
				game_end.addStartHuRight(rv[0]);

				game_end.addProvidePlayer(GRR._provider[i]);
				game_end.addGameScore(GRR._game_score[i]);// 放炮的人？
				game_end.addGangScore(lGangScore[i]);// 杠牌得分
				game_end.addStartHuScore(GRR._start_hu_score[i]);
				game_end.addResultDes(GRR._result_des[i]);

				// 胡牌
				game_end.addWinOrder(GRR._win_order[i]);

				Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					lfs.addItem(GRR._lost_fan_shu[i][j]);
				}

				game_end.addLostFanShu(lfs);

			}

		}

		boolean end = false;
		if (reason == GameConstants.Game_End_NORMAL || reason == GameConstants.Game_End_DRAW) {
			if (_cur_round >= _game_round) {// 局数到了
				end = true;
				game_end.setRoomOverType(1);
				game_end.setPlayerResult(this.process_player_result());
			} else {
				// 确定下局庄家
				// 以后谁胡牌，下局谁做庄。
				// 流局,庄家下一个
				// 通炮,放炮的玩家

			}
		} else if (reason == GameConstants.Game_End_RELEASE_PLAY || reason == GameConstants.Game_End_RELEASE_NO_BEGIN
				|| reason == GameConstants.Game_End_RELEASE_RESULT
				|| reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_SYSTEM) {
			end = true;
			real_reason = GameConstants.Game_End_DRAW;// 刘局
			game_end.setRoomOverType(1);
			game_end.setPlayerResult(this.process_player_result());
		}
		game_end.setEndType(real_reason);

		////////////////////////////////////////////////////////////////////// 得分总的
		roomResponse.setGameEnd(game_end);

		this.send_response_to_room(roomResponse);

		record_game_round(game_end);

		// 结束后刷新玩家
		// RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
		// roomResponse2.setGameStatus(_game_status);
		// roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		// this.load_player_info_data(roomResponse2);
		// this.send_response_to_room(roomResponse2);

		// 超时解散
		if (reason == GameConstants.Game_End_RELEASE_PLAY_TIME_OUT
				|| reason == GameConstants.Game_End_RELEASE_WAIT_TIME_OUT) {
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				Player player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}
		}

		if (end)// 删除
		{
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
		}
		GRR = null;

		// 错误断言
		return false;
	}

	public int analyse_chi_hu_card(int cards_index[], WeaveItem weaveItems[], int weave_count, int cur_card,
			ChiHuRight chiHuRight, int card_type) {

		if (GameConstants.GAME_TYPE_ZZ == _game_type_index) {
			return analyse_chi_hu_card_zz(cards_index, weaveItems, weave_count, cur_card, chiHuRight, card_type);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			return analyse_chi_hu_card_hz(cards_index, weaveItems, weave_count, cur_card, chiHuRight, card_type);
		} else if (GameConstants.GAME_TYPE_CS == _game_type_index) {
			return analyse_chi_hu_card_cs(cards_index, weaveItems, weave_count, cur_card, chiHuRight, card_type);
		} else if (GameConstants.GAME_TYPE_SHUANGGUI == _game_type_index) {
			return analyse_chi_hu_card_sg(cards_index, weaveItems, weave_count, cur_card, chiHuRight, card_type);
		} else if (is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			return analyse_chi_hu_card_zhuzhou(cards_index, weaveItems, weave_count, cur_card, chiHuRight, card_type);
		} else if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {
			return analyse_chi_hu_card_xthh(cards_index, weaveItems, weave_count, cur_card, chiHuRight, card_type);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			// return analyse_chi_hu_card_ay(cards_index, weaveItems,
			// weave_count, cur_card, chiHuRight,card_type);

			return 0;
		}
		return 0;
	}

	/***
	 * 河南麻将胡牌解析
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_henan(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		if ((!has_rule(GameConstants.GAME_RULE_HENAN_HENAN_PAO_HU) && (card_type == GameConstants.HU_CARD_TYPE_PAOHU)))// 如果胡牌类型是炮胡
																														// 但没这个规则
																														// return
		{
			return GameConstants.WIK_NULL;

		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;
		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		int igc_count = _logic.magic_count(cbCardIndexTemp);
		igc_count %= 2;

		if (igc_count == 0) {
			long qxd = _logic.is_qi_xiao_dui_henan(cbCardIndexTemp, weaveItems, weaveCount);
			if (qxd != GameConstants.WIK_NULL) {
				cbChiHuKind = GameConstants.WIK_CHI_HU;
				if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
					chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
				} else {
					chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
				}
				chiHuRight.opr_or(GameConstants.CHR_HENAN_QI_XIAO_DUI);// 都是七小对
			}
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {// 带混 4混
			if ((cards_index[_logic.get_magic_card_index(0)] == 4)
					|| ((cards_index[_logic.get_magic_card_index(0)] == 3) && (_logic.is_magic_card(cur_card))
							&& (card_type == GameConstants.HU_CARD_TYPE_ZIMO
									|| card_type == GameConstants.HU_CARD_TYPE_GANG_KAI))) {
				cbChiHuKind = GameConstants.WIK_CHI_HU;
				if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
					chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
					chiHuRight.opr_or(GameConstants.CHR_HENAN_QISHOU_HU);
				} else {
					// 这个没必要。一定是自摸
					chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
					chiHuRight.opr_or(GameConstants.CHR_HENAN_QISHOU_HU);
				}
			}
		}

		if (chiHuRight.is_empty() == false) {
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else if (card_type == GameConstants.HU_CARD_TYPE_PAOHU) {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			} else if (card_type == GameConstants.HU_CARD_TYPE_QIANGGANG) {
				chiHuRight.opr_or(GameConstants.CHR_HENAN_QIANG_GANG_HU);
			} else if (card_type == GameConstants.HU_CARD_TYPE_TIAN_HU) {
				chiHuRight.opr_or(GameConstants.CHR_HENAN_TIAN_HU);
			} else if (card_type == GameConstants.HU_CARD_TYPE_GANG_KAI) {
				chiHuRight.opr_or(GameConstants.CHR_HENAN_GANG_KAI);
			}
			return cbChiHuKind;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray,
				has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG));
		if (!bValue) {

			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}
		boolean cheng_ju = true;
		if (_logic.is_magic_card(cur_card) && (card_type == GameConstants.HU_CARD_TYPE_PAOHU)) {

			// 胡牌分析
			// 牌型分析 现在没有这个选项
			for (int i = 0; i < analyseItemArray.size(); i++) {
				cheng_ju = false;

				// 变量定义
				AnalyseItem pAnalyseItem = analyseItemArray.get(i);
				if ((pAnalyseItem.bMagicEye == true) && (pAnalyseItem.cbCardEye == cur_card)) {
					cheng_ju = true;
					break;
				}

				for (int j = 0; j < 4; j++) {
					if ((pAnalyseItem.cbCardData[j][0] == cur_card) || (pAnalyseItem.cbCardData[j][1] == cur_card)
							|| (pAnalyseItem.cbCardData[j][2] == cur_card)) {
						if (pAnalyseItem.cbWeaveKind[j] == GameConstants.WIK_PENG) {
							if ((pAnalyseItem.cbCardData[j][0] == pAnalyseItem.cbCardData[j][1])
									&& (pAnalyseItem.cbCardData[j][1] == pAnalyseItem.cbCardData[j][2])
									&& (pAnalyseItem.cbCardData[j][2] == pAnalyseItem.cbCardData[j][0])) {
								cheng_ju = true;
							}

						} else if (pAnalyseItem.cbWeaveKind[j] == GameConstants.WIK_LEFT) {
							if ((pAnalyseItem.cbCardData[j][0] == pAnalyseItem.cbCardData[j][1] - 1)
									&& (pAnalyseItem.cbCardData[j][1] == pAnalyseItem.cbCardData[j][2] - 1)
									&& (pAnalyseItem.cbCardData[j][2] == pAnalyseItem.cbCardData[j][0] + 2)) {
								cheng_ju = true;
							}
						}

					}

					if (cheng_ju == true) {
						break;// 癞子成句
					}
				}

				if (cheng_ju == true) {
					break;// 癞子成句
				}
			}
		}

		if (cheng_ju == false) {
			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;
		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else if (card_type == GameConstants.HU_CARD_TYPE_PAOHU) {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
		} else if (card_type == GameConstants.HU_CARD_TYPE_QIANGGANG) {
			chiHuRight.opr_or(GameConstants.CHR_HENAN_QIANG_GANG_HU);
		} else if (card_type == GameConstants.HU_CARD_TYPE_TIAN_HU) {
			chiHuRight.opr_or(GameConstants.CHR_HENAN_TIAN_HU);
		} else if (card_type == GameConstants.HU_CARD_TYPE_GANG_KAI) {
			chiHuRight.opr_or(GameConstants.CHR_HENAN_GANG_KAI);
		}

		return cbChiHuKind;
	}

	/***
	 * 红中麻将胡牌解析
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_hz(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		// card_type 1 zimo 2 paohu 3qiangganghu
		if ((has_rule(GameConstants.GAME_RULE_HUNAN_ZIMOHU) && (card_type == GameConstants.HU_CARD_TYPE_PAOHU)))// 是否选择了自摸胡||(is_mj_type(MJGameConstants.GAME_TYPE_HZ)&&
																												// !bSelfSendCard)
		{
			return GameConstants.WIK_NULL;

		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		if (has_rule(GameConstants.GAME_RULE_HUNAN_QIDUI)) {
			// 七小对牌 豪华七小对
			long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItems, weaveCount, cur_card);
			if (qxd != GameConstants.WIK_NULL) {
				cbChiHuKind = GameConstants.WIK_CHI_HU;
				if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
					// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
					chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
				} else {
					chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

				}
				// 红中七小不算大胡
				// chiHuRight.opr_or(qxd);
			}

		}

		if ((cards_index[_logic.switch_to_card_index(GameConstants.HZ_MAGIC_CARD)] == 4)
				|| ((cards_index[_logic.switch_to_card_index(GameConstants.HZ_MAGIC_CARD)] == 3)
						&& (cur_card == GameConstants.HZ_MAGIC_CARD))) {

			cbChiHuKind = GameConstants.WIK_CHI_HU;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				// 这个没必要。一定是自摸
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
		}

		// 设置变量
		// chiHuRight.set_empty();
		// chiHuRight.opr_or(MJGameConstants.CHR_SHU_FAN);

		// 抢杠胡
		// if (_current_player == MJGameConstants.INVALID_SEAT && _status_gang
		// && (cbChiHuKind == MJGameConstants.WIK_CHI_HU)) {
		// if (has_rule(MJGameConstants.GAME_TYPE_ZZ_QIANGGANGHU))// 是否选择了抢杠胡
		// {
		// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		// chiHuRight.opr_or(MJGameConstants.CHR_QIANG_GANG_HU);
		// } else {
		// chiHuRight.set_empty();
		// return MJGameConstants.WIK_NULL;
		// }
		//
		// }

		if (chiHuRight.is_empty() == false) {

			return cbChiHuKind;
		}

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, true);
		if (!bValue) {

			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			// cbChiHuKind = MJGameConstants.WIK_CHI_HU;

			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

		}

		return cbChiHuKind;
	}

	/**
	 * 双鬼麻将 自能自摸
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_sg(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		// card_type 1 zimo 2 paohu 3qiangganghu
		if (card_type != GameConstants.HU_CARD_TYPE_ZIMO)//
		{
			return GameConstants.WIK_NULL;

		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		// if (has_rule(MJGameConstants.GAME_TYPE_ZZ_QIDUI)){
		// // 七小对牌 豪华七小对
		// long qxd =
		// _logic.is_qi_xiao_dui(cards_index,weaveItems,weaveCount,cur_card);
		// if(qxd!=MJGameConstants.WIK_NULL ) {
		// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		// if (card_type== MJGameConstants.HU_CARD_TYPE_ZIMO) {
		// //cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		//
		// chiHuRight.opr_or(MJGameConstants.CHR_ZI_MO);
		// }else{
		// chiHuRight.opr_or(MJGameConstants.CHR_SHU_FAN);
		//
		//
		// }
		//
		// //双鬼没大胡
		// chiHuRight.opr_or(qxd);
		// }
		//
		// }

		// if(chiHuRight.is_empty()==false){
		//
		// return cbChiHuKind;
		// }

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		// 大于6个就胡 先这样处理
		if (_logic.magic_count(cbCardIndexTemp) >= 6) {

			cbChiHuKind = GameConstants.WIK_CHI_HU;

			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);

			return cbChiHuKind;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, false);
		if (!bValue) {

			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		chiHuRight.opr_or(GameConstants.CHR_ZI_MO);

		return cbChiHuKind;
	}

	/**
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_xthh(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		// card_type 1 zimo 2 paohu 3qiangganghu

		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		int lai_zi_count = cards_index[_logic.get_magic_card_index(0)];
		if (_logic.is_magic_card(cur_card)) {
			lai_zi_count++;// 摸的牌是癞子
		}

		// 一赖到底。只能有一张癞子
		if (has_rule(GameConstants.GAME_RULE_HEBEI_YI_LAI_DAO_DI)) {
			if (lai_zi_count > 1) {
				return GameConstants.WIK_NULL;
			}
		}

		/**
		 * 
		 * 放铳 ：胡牌的玩家手上没有赖子，牌桌上也没有打出赖子。玩家打出你要胡的字，可以吃胡。
		 * 
		 * 抢扛胡：有玩家碰牌后，又摸了一个碰牌的字，准备杠时，可以抢胡（如A胡一条，但是B碰了一条，然后B又摸了一条准备杠时，A可以抢杠胡牌），前提条件为：胡牌的玩家手上没有赖子，牌桌上也没有打出赖子
		 * 
		 * 热铳
		 * ：玩家在暗杠.点杠,回头杠,任一杠牌过后打出来的字为需要胡牌的字，则为热铳。（只要杠过后打出来胡牌的都为热铳，手上没有赖子，牌桌上也没有打出赖子
		 * 才能胡热铳）
		 * 
		 */
		if (card_type != GameConstants.HU_CARD_TYPE_ZIMO) {
			// 放铳、抢杠胡、热铳,手上有癞子或打过癞子,都不能胡
			if (GRR._piao_lai_count > 0 || lai_zi_count > 0) {
				return GameConstants.WIK_NULL;
			}
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		// if (has_rule(MJGameConstants.GAME_TYPE_ZZ_QIDUI)){
		// // 七小对牌 豪华七小对 不成顺子。有癞子也是当其他牌
		// //TODO
		// //两个癞子的情况需要特殊处理
		// long qxd =
		// _logic.is_qi_xiao_dui(cards_index,weaveItems,weaveCount,cur_card);
		// if(qxd!=MJGameConstants.WIK_NULL ) {
		// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		// if (card_type== MJGameConstants.HU_CARD_TYPE_ZIMO) {
		// if(lai_zi_count>0){
		// //软摸
		// chiHuRight.opr_or(MJGameConstants.CHR_HH_RUAN_MO);
		// }else{
		// //黑摸
		// chiHuRight.opr_or(MJGameConstants.CHR_HH_HEI_MO);
		// }
		//
		// }else if(card_type== MJGameConstants.HU_CARD_TYPE_PAOHU){
		// //抓铳
		// chiHuRight.opr_or(MJGameConstants.CHR_HH_ZHUO_CHONG);
		//
		// }else if(card_type== MJGameConstants.HU_CARD_TYPE_QIANGGANG){
		// //抓铳
		// chiHuRight.opr_or(MJGameConstants.CHR_HH_ZHUO_CHONG);
		//
		// }else if(card_type== MJGameConstants.HU_CARD_TYPE_RE_CHONG){
		// //热铳
		// chiHuRight.opr_or(MJGameConstants.CHR_HH_RE_CHONG);
		//
		// }
		//
		// //没大胡
		// chiHuRight.opr_or(qxd);
		// }
		//
		// }
		//
		//
		// if(chiHuRight.is_empty()==false){
		//
		// return cbChiHuKind;
		// }

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, false);
		if (!bValue) {
			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		boolean cheng_ju = true;

		// 胡牌分析
		// 牌型分析 现在没有这个选项
		for (int i = 0; i < analyseItemArray.size(); i++) {
			cheng_ju = true;

			// 变量定义
			AnalyseItem pAnalyseItem = analyseItemArray.get(i);
			if (pAnalyseItem.bMagicEye == true) {
				cheng_ju = false;
				continue;
			}

			for (int j = 0; j < 4; j++) {

				if (pAnalyseItem.cbWeaveKind[j] == GameConstants.WIK_PENG) {
					if ((pAnalyseItem.cbCardData[j][0] != pAnalyseItem.cbCardData[j][1])
							|| (pAnalyseItem.cbCardData[j][1] != pAnalyseItem.cbCardData[j][2])
							|| (pAnalyseItem.cbCardData[j][2] != pAnalyseItem.cbCardData[j][0])) {
						cheng_ju = false;
					}

				} else if (pAnalyseItem.cbWeaveKind[j] == GameConstants.WIK_LEFT) {
					if ((pAnalyseItem.cbCardData[j][0] != pAnalyseItem.cbCardData[j][1] - 1)
							|| (pAnalyseItem.cbCardData[j][1] != pAnalyseItem.cbCardData[j][2] - 1)
							|| (pAnalyseItem.cbCardData[j][2] != pAnalyseItem.cbCardData[j][0] + 2)) {
						cheng_ju = false;
					}
				}

				if (cheng_ju == false) {
					break;// 癞子成句
				}
			}

			if (cheng_ju == true) {
				break;// 癞子成句
			}
		}

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			if (cheng_ju == true) {
				// 黑摸
				chiHuRight.opr_or(GameConstants.CHR_HUBEI_HEI_MO);
			} else {
				// 软摸
				chiHuRight.opr_or(GameConstants.CHR_HUBEI_RUAN_MO);
			}

		} else if (card_type == GameConstants.HU_CARD_TYPE_PAOHU) {
			// 抓铳
			chiHuRight.opr_or(GameConstants.CHR_HUBEI_ZHUO_CHONG);

		} else if (card_type == GameConstants.HU_CARD_TYPE_QIANGGANG) {
			// 抢杠胡
			chiHuRight.opr_or(GameConstants.CHR_HUBEI_QIANG_GANG_HU);

		} else if (card_type == GameConstants.HU_CARD_TYPE_RE_CHONG) {
			// 热铳
			chiHuRight.opr_or(GameConstants.CHR_HUBEI_RE_CHONG);

		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;
		return cbChiHuKind;
	}

	/**
	 * 转转麻将
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_zz(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {

		if ((has_rule(GameConstants.GAME_RULE_HUNAN_QIANGGANGHU) == false)
				&& (card_type == GameConstants.HU_CARD_TYPE_PAOHU))// 是否选择了自摸胡||(is_mj_type(MJGameConstants.GAME_TYPE_HZ)&&
																	// !bSelfSendCard)
		{
			return GameConstants.WIK_NULL;
		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		if (has_rule(GameConstants.GAME_RULE_HUNAN_QIDUI)) {
			// 七小对牌 豪华七小对
			long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItems, weaveCount, cur_card);
			if (qxd != GameConstants.WIK_NULL) {
				cbChiHuKind = GameConstants.WIK_CHI_HU;
				if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
					// cbChiHuKind = MJGameConstants.WIK_CHI_HU;

					chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
				} else {
					chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

				}

				// 转转没大胡
				// chiHuRight.opr_or(qxd);
			}

		}

		// if(is_mj_type(MJGameConstants.GAME_TYPE_HZ)){
		// if((cards_index[_logic.switch_to_card_index(MJGameConstants.ZZ_MAGIC_CARD)]==4)||
		// ((cards_index[_logic.switch_to_card_index(MJGameConstants.ZZ_MAGIC_CARD)]==3)
		// && (cur_card == MJGameConstants.ZZ_MAGIC_CARD))){
		//
		// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		// if (card_type == MJGameConstants.HU_CARD_TYPE_ZIMO) {
		// chiHuRight.opr_or(MJGameConstants.CHR_ZI_MO);
		// }else{
		// //这个没必要。一定是自摸
		// chiHuRight.opr_or(MJGameConstants.CHR_SHU_FAN);
		// }
		// }
		//
		// }

		// 设置变量
		// chiHuRight.set_empty();
		// chiHuRight.opr_or(MJGameConstants.CHR_SHU_FAN);

		// 抢杠胡
		if (_current_player == GameConstants.INVALID_SEAT && _status_gang
				&& (cbChiHuKind == GameConstants.WIK_CHI_HU)) {
			if (has_rule(GameConstants.GAME_RULE_HUNAN_QIANGGANGHU))// 是否选择了抢杠胡
			{
				cbChiHuKind = GameConstants.WIK_CHI_HU;
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);
			} else {
				chiHuRight.set_empty();
				return GameConstants.WIK_NULL;
			}

		}

		if (chiHuRight.is_empty() == false) {

			return cbChiHuKind;
		}

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();

		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, false);
		if (!bValue) {

			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		// 胡牌分析
		// 牌型分析 现在没有这个选项
		// for (int i=0;i<analyseItemArray.size();i++)
		// {
		// //变量定义
		// AnalyseItem pAnalyseItem=analyseItemArray.get(i);
		// if (has_rule(MJGameConstants.GAME_TYPE_ZZ_258))
		// {
		// int cbCardValue = _logic.get_card_value(pAnalyseItem.cbCardEye);
		// if( cbCardValue != 2 && cbCardValue != 5 && cbCardValue != 8 )
		// {
		// continue;
		// }
		// }
		// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		// break;
		// }

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			// cbChiHuKind = MJGameConstants.WIK_CHI_HU;

			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

		}

		return cbChiHuKind;
	}

	/***
	 * 河南红中麻将胡牌解析
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_hnhz(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		// card_type 1 zimo 2 paohu 3qiangganghu
		if ((has_rule(GameConstants.GAME_RULE_HENAN_ZIMOHU) && (card_type == GameConstants.HU_CARD_TYPE_PAOHU)))// 是否选择了自摸胡||(is_mj_type(MJGameConstants.GAME_TYPE_HZ)&&
																												// !bSelfSendCard)
		{
			return GameConstants.WIK_NULL;

		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		// 七小对牌 豪华七小对
		long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItems, weaveCount, cur_card);
		if (qxd != GameConstants.WIK_NULL) {
			cbChiHuKind = GameConstants.WIK_CHI_HU;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

			}
			// 红中七小不算大胡
			// chiHuRight.opr_or(qxd);
		}

		if ((cards_index[_logic.switch_to_card_index(GameConstants.HZ_MAGIC_CARD)] == 4)
				|| ((cards_index[_logic.switch_to_card_index(GameConstants.HZ_MAGIC_CARD)] == 3)
						&& (cur_card == GameConstants.HZ_MAGIC_CARD))) {

			cbChiHuKind = GameConstants.WIK_CHI_HU;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				// 这个没必要。一定是自摸
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
		}

		if (chiHuRight.is_empty() == false) {

			return cbChiHuKind;
		}

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, true);
		if (!bValue) {

			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

		}

		return cbChiHuKind;
	}

	
	/**
	 * 韶关麻将 胡牌分析
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_shaoguan(int cards_index[], WeaveItem weaveItems[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {

		if (card_type == GameConstants.HU_CARD_TYPE_PAOHU)
		{
			return GameConstants.WIK_NULL;
		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		boolean hu=false;
		
		// 七小对牌 豪华七小对
		long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItems, weaveCount, cur_card);
		if (qxd != GameConstants.WIK_NULL) {
			cbChiHuKind = GameConstants.WIK_CHI_HU;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

			}
			hu=true;
		}
		
		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, false);
		if (!bValue) {

			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}
		
		
		// 牌型分析
		for (int i = 0; i < analyseItemArray.size(); i++) {
			// 变量定义
			AnalyseItem analyseItem = analyseItemArray.get(i);
			/*
			 * // 判断番型
			 */
			// 碰碰和
			if (_logic.is_pengpeng_hu(analyseItem)) {
				chiHuRight.opr_or(GameConstants.CHR_GD_PENGPENGHU);
				hu = true;
				break;
			}

		}
		
		
		// 清一色牌
		if (_logic.is_qing_yi_se(cards_index, weaveItems, weaveCount, cur_card)) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_QING_YI_SE);
			hu = true;
		}
		
		

		if (hu == true) {
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
			// 有大胡
			return GameConstants.WIK_CHI_HU;
		}

		// 胡牌分析 有没有258
		for (int i = 0; i < analyseItemArray.size(); i++) {
			// 变量定义
			AnalyseItem pAnalyseItem = analyseItemArray.get(i);
			int cbCardValue = _logic.get_card_value(pAnalyseItem.cbCardEye);
			if (cbCardValue != 2 && cbCardValue != 5 && cbCardValue != 8) {
				continue;
			}

			hu = true;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
			return GameConstants.WIK_CHI_HU;
		}
		// chiHuRight.set_empty();
		//return GameConstants.WIK_NULL;


		// 设置变量
		// chiHuRight.set_empty();
		// chiHuRight.opr_or(MJGameConstants.CHR_SHU_FAN);

		// 抢杠胡
		if (_current_player == GameConstants.INVALID_SEAT && _status_gang
				&& (cbChiHuKind == GameConstants.WIK_CHI_HU)) {
			if (has_rule(GameConstants.GAME_RULE_HUNAN_QIANGGANGHU))// 是否选择了抢杠胡
			{
				cbChiHuKind = GameConstants.WIK_CHI_HU;
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);
			} else {
				chiHuRight.set_empty();
				return GameConstants.WIK_NULL;
			}

		}

		if (chiHuRight.is_empty() == false) {

			return cbChiHuKind;
		}

		

		

		// 胡牌分析
		// 牌型分析 现在没有这个选项
		// for (int i=0;i<analyseItemArray.size();i++)
		// {
		// //变量定义
		// AnalyseItem pAnalyseItem=analyseItemArray.get(i);
		// if (has_rule(MJGameConstants.GAME_TYPE_ZZ_258))
		// {
		// int cbCardValue = _logic.get_card_value(pAnalyseItem.cbCardEye);
		// if( cbCardValue != 2 && cbCardValue != 5 && cbCardValue != 8 )
		// {
		// continue;
		// }
		// }
		// cbChiHuKind = MJGameConstants.WIK_CHI_HU;
		// break;
		// }

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			// cbChiHuKind = MJGameConstants.WIK_CHI_HU;

			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);

		}

		return cbChiHuKind;
	}
	/**
	 * 红中麻将获取听牌
	 * 
	 * @param cards
	 * @param cards_index
	 * @param weaveItem
	 * @param cbWeaveCount
	 * @return
	 */
	public int get_hz_ting_card(int[] cards, int cards_index[], WeaveItem weaveItem[], int cbWeaveCount,
			boolean dai_feng) {

		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();

		int count = 0;
		int cbCurrentCard;

		int l = GameConstants.MAX_INDEX - GameConstants.CARD_HUA_COUNT - GameConstants.CARD_FENG_COUNT;
		int ql = l;
		if (dai_feng) {
			l += GameConstants.CARD_FENG_COUNT;
			ql += (GameConstants.CARD_FENG_COUNT - 1);
		}
		for (int i = 0; i < l; i++) {
			if (this._logic.is_magic_index(i))
				continue;
			cbCurrentCard = _logic.switch_to_card_data(i);
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_hz(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard;
				count++;
			}
		}

		l -= 1;
		if (count == 0) {
			// 红中能不能胡
			// cbCurrentCard =
			// _logic.switch_to_card_data(this._logic.get_magic_card_index(0));
			// if( MJGameConstants.WIK_CHI_HU == analyse_chi_hu_card_hz(
			// cbCardIndexTemp,weaveItem,cbWeaveCount,cbCurrentCard,chr,MJGameConstants.HU_CARD_TYPE_ZIMO
			// ) ){
			// cards[count] = cbCurrentCard;
			// count++;
			// }
		} else if (count > 0 && count < ql) {
			// 有胡的牌。红中肯定能胡
			cards[count] = _logic.switch_to_card_data(this._logic.get_magic_card_index(0));
			count++;
		} else {
			// 全听
			count = 1;
			cards[0] = -1;
		}

		return count;
	}

	/**
	 * 河南麻将获取听牌
	 * 
	 * @param cards
	 * @param cards_index
	 * @param weaveItem
	 * @param cbWeaveCount
	 * @return
	 */
	public int get_henan_ting_card(int[] cards, int cards_index[], WeaveItem weaveItem[], int cbWeaveCount) {

		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();

		int count = 0;
		int cbCurrentCard;

		int del = 0;

		boolean isDaiFeng = has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG);
		int mj_count = GameConstants.MAX_ZI;
		if (isDaiFeng) {
			mj_count = GameConstants.MAX_ZI_FENG;
		} else {
			mj_count = GameConstants.MAX_ZI;
		}

		for (int i = 0; i < mj_count; i++) {
//			if (this._logic.is_magic_index(i))
//				continue;
			cbCurrentCard = _logic.switch_to_card_data(i);
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_henan(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard;
				if (this._logic.is_magic_index(i)){
					if (chr.opr_and(GameConstants.CHR_HENAN_QI_XIAO_DUI).is_empty()) {
						cards[count]+= GameConstants.CARD_ESPECIAL_TYPE_HUN;
					}
				}
				count++;
			}
		}
		
		

		// 有胡的牌。癞子肯定能胡
		if (count > 0) {
//			if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {
//				cards[count] = _logic.switch_to_card_data(this._logic.get_magic_card_index(0))
//						+ GameConstants.CARD_ESPECIAL_TYPE_HUN;
//				count++;
//			}
		} else {
//			if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {
//				// 看看鬼牌能不能胡
//				cbCurrentCard = _logic.switch_to_card_data(this._logic.get_magic_card_index(0));
//				chr.set_empty();
//				if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_henan(cbCardIndexTemp, weaveItem, cbWeaveCount,
//						cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
//					cards[count] = cbCurrentCard + GameConstants.CARD_ESPECIAL_TYPE_HUN;
//					count++;
//				}
//			}
		}

		int number = isDaiFeng ? 34 : 27;
		if (count >= number) {
			count = 1;
			cards[0] = -1;
		}

		return count;
	}

	/**
	 * 安阳麻将 胡牌检测
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_ay(int seat_index, int cards_index[], WeaveItem weaveItems[], int weaveCount,
			int cur_card, ChiHuRight chiHuRight, int card_type) {
		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItems, weaveCount, cur_card);
		if (qxd != GameConstants.WIK_NULL) {
			cbChiHuKind = GameConstants.WIK_CHI_HU;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
			chiHuRight.opr_or(GameConstants.CHR_HENAN_DAN_DIAO);
		}

		if (chiHuRight.is_empty() == false) {

			return cbChiHuKind;
		}

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		boolean has_feng = false;
		if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
			has_feng = true;
		}
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, has_feng);
		if (!bValue) {
			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		boolean ka_bian = false;
		if (_playerStatus[seat_index]._hu_card_count == 1) {// 1.只胡一张；卡边吊：两嘴
			for (int i = 0; i < analyseItemArray.size(); i++) {
				// 变量定义
				AnalyseItem pAnalyseItem = analyseItemArray.get(i);
				for (int j = 0; j < 4; j++) {
					if (pAnalyseItem.cbWeaveKind[j] != GameConstants.WIK_LEFT) {
						continue;
					}
					if (pAnalyseItem.cbCenterCard[j] == (cur_card - 1)) {
						// 如果是中间的牌
						chiHuRight.opr_or(GameConstants.CHR_HENAN_KA_ZHANG);
						ka_bian = true;
						break;
					} else {
						// 边张
						int cbCardValue = _logic.get_card_value(pAnalyseItem.cbCenterCard[j]);
						if ((cbCardValue == 1) && (pAnalyseItem.cbCenterCard[j] == (cur_card - 2))) {
							chiHuRight.opr_or(GameConstants.CHR_HENAN_BIAN_ZHANG);
							ka_bian = true;
							break;
						} else if ((cbCardValue == 7) && (pAnalyseItem.cbCenterCard[j] == cur_card)) {
							chiHuRight.opr_or(GameConstants.CHR_HENAN_BIAN_ZHANG);
							ka_bian = true;
							break;
						}
					}

					if (ka_bian == true)
						break;
				}
			}

			if (ka_bian == false) {
				// 单
				chiHuRight.opr_or(GameConstants.CHR_HENAN_DAN_DIAO);
			}
		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
		}

		return cbChiHuKind;
	}

	/**
	 * 林州麻将 胡牌检测
	 * 
	 * @param cards_index
	 * @param weaveItems
	 * @param weaveCount
	 * @param cur_card
	 * @param chiHuRight
	 * @param card_type
	 * @return
	 */
	public int analyse_chi_hu_card_lz(int seat_index, int cards_index[], WeaveItem weaveItems[], int weaveCount,
			int cur_card, ChiHuRight chiHuRight, int card_type) {
		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0) {
			return GameConstants.WIK_NULL;
		}

		// 变量定义
		int cbChiHuKind = GameConstants.WIK_NULL;

		long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItems, weaveCount, cur_card);
		// boolean hu=false;//是否胡的标记--//七小对 将将胡 可能不是能胡牌的牌型 优先判断
		if (qxd != GameConstants.WIK_NULL) {
			if (qxd == GameConstants.CHR_HUNAN_SHUANG_HAO_HUA_QI_XIAO_DUI) {
				chiHuRight.opr_or(GameConstants.CHR_HENAN_HH_QI_XIAO_DUI);
			} else {
				chiHuRight.opr_or(qxd);
			}

			// hu = true;
		}

		if (chiHuRight.is_empty() == false) {
			cbChiHuKind = GameConstants.WIK_CHI_HU;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else if (card_type == GameConstants.HU_CARD_TYPE_PAOHU) {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			} else if (card_type == GameConstants.HU_CARD_TYPE_QIANGGANG) {
				chiHuRight.opr_or(GameConstants.CHR_HENAN_QIANG_GANG_HU);
			} else if (card_type == GameConstants.HU_CARD_TYPE_TIAN_HU) {
				chiHuRight.opr_or(GameConstants.CHR_HENAN_TIAN_HU);
			}
			return cbChiHuKind;
		}

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		if (cur_card != GameConstants.INVALID_VALUE) {
			cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;
		}

		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		boolean has_feng = false;
		if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
			has_feng = true;
		}
		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItems, weaveCount, analyseItemArray, has_feng);
		if (!bValue) {
			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		cbChiHuKind = GameConstants.WIK_CHI_HU;

		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else if (card_type == GameConstants.HU_CARD_TYPE_PAOHU) {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
		} else if (card_type == GameConstants.HU_CARD_TYPE_QIANGGANG) {
			chiHuRight.opr_or(GameConstants.CHR_HENAN_QIANG_GANG_HU);
		} else if (card_type == GameConstants.HU_CARD_TYPE_TIAN_HU) {
			chiHuRight.opr_or(GameConstants.CHR_HENAN_TIAN_HU);
		}

		return cbChiHuKind;
	}

	public int get_sg_ting_card(int[] cards, int cards_index[], WeaveItem weaveItem[], int cbWeaveCount) {

		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();

		// int cbCurrentCard =
		// _logic.switch_to_card_data(this._logic.get_magic_card_index());
		// if( MJGameConstants.WIK_CHI_HU == analyse_chi_hu_card_hz(
		// cbCardIndexTemp,weaveItem,cbWeaveCount,cbCurrentCard,chr,true ) ){
		// cards[count] = cbCurrentCard;
		// count++;
		//
		// // cards[0] = -1;
		// }
		int count = 0;
		int cbCurrentCard;
		for (int i = 0; i < GameConstants.MAX_INDEX - GameConstants.CARD_HUA_COUNT
				- GameConstants.CARD_FENG_COUNT; i++) {
			if (this._logic.is_magic_index(i))
				continue;
			cbCurrentCard = _logic.switch_to_card_data(i);
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_sg(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard;
				count++;
			}
		}

		// 有胡的牌。癞子肯定能胡
		if (count > 0) {
			cards[count] = _logic.switch_to_card_data(this._logic.get_magic_card_index(0))
					+ GameConstants.CARD_ESPECIAL_TYPE_GUI;
			count++;
			cards[count] = _logic.switch_to_card_data(this._logic.get_magic_card_index(1))
					+ GameConstants.CARD_ESPECIAL_TYPE_GUI;
			count++;
		} else {
			// 看看鬼牌能不能胡
			cbCurrentCard = _logic.switch_to_card_data(this._logic.get_magic_card_index(0));
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_sg(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard + GameConstants.CARD_ESPECIAL_TYPE_GUI;
				count++;

				cbCurrentCard = _logic.switch_to_card_data(this._logic.get_magic_card_index(1));
				cards[count] = cbCurrentCard + GameConstants.CARD_ESPECIAL_TYPE_GUI;
				count++;
			}

			// cbCurrentCard = _logic.switch_to_card_data(
			// this._logic.get_magic_card_index(1) );
			// cards[count] =
			// cbCurrentCard+MJGameConstants.CARD_ESPECIAL_TYPE_GUI;
			// count++;
			// chr.set_empty();
			// if( MJGameConstants.WIK_CHI_HU == analyse_chi_hu_card_hz(
			// cbCardIndexTemp,weaveItem,cbWeaveCount,cbCurrentCard,chr,
			// MJGameConstants.HU_CARD_TYPE_ZIMO ) ){
			// cards[count] =
			// cbCurrentCard+MJGameConstants.CARD_ESPECIAL_TYPE_GUI;
			// count++;
			// }

		}

		if (count >= 27) {
			count = 1;
			cards[0] = -1;
		}

		return count;
	}

	public int get_xthh_ting_card(int[] cards, int cards_index[], WeaveItem weaveItem[], int cbWeaveCount) {
		boolean has_laizi = false;
		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
			if (cards_index[i] > 0 && _logic.is_magic_index(i)) {
				has_laizi = true;
			}
		}

		ChiHuRight chr = new ChiHuRight();

		// int cbCurrentCard =
		// _logic.switch_to_card_data(this._logic.get_magic_card_index());
		// if( MJGameConstants.WIK_CHI_HU == analyse_chi_hu_card_hz(
		// cbCardIndexTemp,weaveItem,cbWeaveCount,cbCurrentCard,chr,true ) ){
		// cards[count] = cbCurrentCard;
		// count++;
		//
		// // cards[0] = -1;
		// }
		int count = 0;
		int cbCurrentCard;
		for (int i = 0; i < GameConstants.MAX_INDEX - GameConstants.CARD_FENG_COUNT
				- GameConstants.CARD_HUA_COUNT; i++) {
			if (this._logic.is_magic_index(i))
				continue;
			cbCurrentCard = _logic.switch_to_card_data(i);
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_xthh(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard;
				count++;
			}
		}

		if (has_rule(GameConstants.GAME_RULE_HEBEI_GAN_DENG_YAN)) {
			// 有胡的牌。癞子肯定能胡
			if (count > 0) {
				cards[count] = _logic.switch_to_card_data(this._logic.get_magic_card_index(0))
						+ GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
				count++;
			} else {
				// 不能胡，看看癞子能不能胡
				cbCurrentCard = _logic.switch_to_card_data(this._logic.get_magic_card_index(0));
				chr.set_empty();
				if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_xthh(cbCardIndexTemp, weaveItem, cbWeaveCount,
						cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
					cards[count] = cbCurrentCard + GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
					count++;
				}
			}

			if (count >= 27) {
				count = 1;
				cards[0] = -1;
			}

		} else if (has_rule(GameConstants.GAME_RULE_HEBEI_YI_LAI_DAO_DI)) {
			if (has_laizi) {
				// 有胡的牌。癞子肯定能胡
				// if(count>0){
				// cards[count] =
				// _logic.switch_to_card_data(this._logic.get_magic_card_index(0))+MJGameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
				// count++;
				// }

				if (count >= 26) {
					count = 1;
					cards[0] = -1;
				}
			} else {
				// 有胡的牌。癞子肯定能胡
				if (count > 0) {
					cards[count] = _logic.switch_to_card_data(this._logic.get_magic_card_index(0))
							+ GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
					count++;
				} else {
					// 不能胡，看看癞子能不能胡
					cbCurrentCard = _logic.switch_to_card_data(this._logic.get_magic_card_index(0));
					chr.set_empty();
					if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_xthh(cbCardIndexTemp, weaveItem, cbWeaveCount,
							cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
						cards[count] = cbCurrentCard + GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
						count++;
					}
				}

				if (count >= 27) {
					count = 1;
					cards[0] = -1;
				}
			}
		}

		return count;
	}

	public int get_ay_ting_card(int seat_index, int[] cards, int cards_index[], WeaveItem weaveItem[],
			int cbWeaveCount) {
		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();
		int cbCurrentCard;
		int count = 0;
		for (int i = 0; i < GameConstants.MAX_INDEX - GameConstants.CARD_HUA_COUNT; i++) {
			if (this._logic.is_magic_index(i))
				continue;
			cbCurrentCard = _logic.switch_to_card_data(i);
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_ay(seat_index, cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard;
				count++;
			}
		}
		return count;
	}

	public int get_lz_ting_card(int seat_index, int[] cards, int cards_index[], WeaveItem weaveItem[],
			int cbWeaveCount) {
		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();
		int cbCurrentCard;
		int count = 0;
		for (int i = 0; i < GameConstants.MAX_INDEX - GameConstants.CARD_HUA_COUNT; i++) {
			if (this._logic.is_magic_index(i))
				continue;
			cbCurrentCard = _logic.switch_to_card_data(i);
			chr.set_empty();
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_ay(seat_index, cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO)) {
				cards[count] = cbCurrentCard;
				count++;
			}
		}
		return count;
	}

	/**
	 * 小胡 1 、大四喜：起完牌后，玩家手上已有四张一样的牌，即可胡牌。（四喜计分等同小胡自摸） 2 、板板胡：起完牌后，玩家手上没有一张 2 、 5
	 * 、 8 （将牌），即可胡牌。（等同小胡自摸） 3 、缺一色：起完牌后，玩家手上筒、索、万任缺一门，即可胡牌。（等同小胡自摸） 4
	 * 、六六顺：起完牌后，玩家手上已有 2 个刻子（刻子：三个一样的牌），即可胡牌。（等同小胡自摸） 5 、平胡： 2 、 5 、 8
	 * 作将，其余成刻子或顺子或一句话，即可胡牌。
	 * 
	 * @param cards_index
	 * @param chiHuRight
	 * @return
	 */
	// 解析吃胡----小胡 长沙玩法
	public int analyse_chi_hu_card_cs_xiaohu(int cards_index[], ChiHuRight chiHuRight) {
		chiHuRight.reset_card();
		int cbChiHuKind = GameConstants.WIK_NULL;

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		boolean bDaSiXi = false;// 大四喜
		boolean bBanBanHu = true;// 板板胡
		int cbQueYiMenColor[] = new int[] { 1, 1, 1 };// 缺一色
		int cbLiuLiuShun = 0;// 六六顺

		// 计算单牌
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			int cbCardCount = cbCardIndexTemp[i];// 数量

			if (cbCardCount == 0) {
				continue;
			}
			// 大四喜：起完牌后，玩家手上已有四张一样的牌，即可胡牌。（四喜计分等同小胡自摸）
			if (cbCardCount == 4) {
				chiHuRight._index_da_si_xi = i;
				bDaSiXi = true;
			}
			// 六六顺：起完牌后，玩家手上已有 2 个刻子（刻子：三个一样的牌），即可胡牌。（等同小胡自摸）
			if (cbCardCount == 3) {
				if (cbLiuLiuShun == 0) {
					chiHuRight._index_liul_liu_shun_1 = i;
				} else if (cbLiuLiuShun == 1) {
					chiHuRight._index_liul_liu_shun_2 = i;
				}
				cbLiuLiuShun++;
			}

			int card = _logic.switch_to_card_data(i);
			int cbValue = _logic.get_card_value(card);
			if (cbValue == 2 || cbValue == 5 || cbValue == 8) {
				// 板板胡：起完牌后，玩家手上 !!没有!! 一张 2 、 5 、 8 （将牌），即可胡牌。（等同小胡自摸）
				bBanBanHu = false;
			}

			// 缺一色：起完牌后，玩家手上筒、索、万任缺一门，即可胡牌。（等同小胡自摸）
			int cbCardColor = _logic.get_card_color(card);
			if (cbCardColor > 2)
				continue;
			cbQueYiMenColor[cbCardColor] = 0;
		}
		if (bDaSiXi) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_XIAO_DA_SI_XI);
			cbChiHuKind = GameConstants.WIK_XIAO_HU;
		}
		if (bBanBanHu) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_XIAO_BAN_BAN_HU);
			cbChiHuKind = GameConstants.WIK_XIAO_HU;
			chiHuRight._show_all = true;
		}
		if ((cbQueYiMenColor[0] == 1) || (cbQueYiMenColor[1] == 1) || (cbQueYiMenColor[2] == 1)) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_XIAO_QUE_YI_SE);
			cbChiHuKind = GameConstants.WIK_XIAO_HU;

			chiHuRight._show_all = true;
		}
		if (cbLiuLiuShun >= 2) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_XIAO_LIU_LIU_SHUN);
			cbChiHuKind = GameConstants.WIK_XIAO_HU;
			// chiHuRight._show_all = true;
		}
		return cbChiHuKind;
	}

	// 解析吃胡 长沙玩法
	public int analyse_chi_hu_card_cs(int cards_index[], WeaveItem weaveItem[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		// 变量定义
		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0)
			return GameConstants.WIK_NULL;
		// 设置变量
		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		// chiHuRight.set_empty();可以重复

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;

		boolean hu = false;// 是否胡的标记--//七小对 将将胡 可能不是能胡牌的牌型 优先判断
		long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItem, weaveCount, cur_card);
		if (qxd != GameConstants.WIK_NULL) {
			chiHuRight.opr_or(qxd);
			hu = true;
		}
		// 将将胡
		if (_logic.is_jiangjiang_hu(cards_index, weaveItem, weaveCount, cur_card)) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_JIANGJIANG_HU);
			hu = true;
		}

		// 分析扑克--通用的判断胡牌方法
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItem, weaveCount, analyseItemArray, false);

		// 胡牌分析
		if (bValue == false) {
			// 不能胡的情况,有可能是七小对
			// 七小对牌 豪华七小对
			if (hu == false) {
				// chiHuRight.set_empty();
				return GameConstants.WIK_NULL;
			}
		}

		/*
		 * // 特殊番型
		 */

		// 全求人
		if (_logic.is_dan_diao(cards_index, cur_card)) {// weaveCount == 4&&
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_QUAN_QIU_REN);
			hu = true;
		}

		// 清一色牌
		if (_logic.is_qing_yi_se(cards_index, weaveItem, weaveCount, cur_card)) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_QING_YI_SE);
			hu = true;
		}

		// 牌型分析
		for (int i = 0; i < analyseItemArray.size(); i++) {
			// 变量定义
			AnalyseItem analyseItem = analyseItemArray.get(i);
			/*
			 * // 判断番型
			 */
			// 碰碰和
			if (_logic.is_pengpeng_hu(analyseItem)) {
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_PENGPENG_HU);
				hu = true;
				break;
			}

		}

		if (hu == true) {
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
			// 有大胡
			return GameConstants.WIK_CHI_HU;
		}

		// 胡牌分析 有没有258
		for (int i = 0; i < analyseItemArray.size(); i++) {
			// 变量定义
			AnalyseItem pAnalyseItem = analyseItemArray.get(i);
			int cbCardValue = _logic.get_card_value(pAnalyseItem.cbCardEye);
			if (cbCardValue != 2 && cbCardValue != 5 && cbCardValue != 8) {
				continue;
			}

			hu = true;
			if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
				chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
			} else {
				chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
			}
			return GameConstants.WIK_CHI_HU;
		}
		// chiHuRight.set_empty();
		return GameConstants.WIK_NULL;
	}

	// 解析吃胡 株洲玩法
	public int analyse_chi_hu_card_zhuzhou(int cards_index[], WeaveItem weaveItem[], int weaveCount, int cur_card,
			ChiHuRight chiHuRight, int card_type) {
		// 变量定义
		if ((has_rule(GameConstants.GAME_RULE_HUNAN_QIANGGANGHU) == false)
				&& (card_type == GameConstants.HU_CARD_TYPE_PAOHU))// 是否选择了自摸胡||(is_mj_type(MJGameConstants.GAME_TYPE_HZ)&&
																	// !bSelfSendCard)
		{
			return GameConstants.WIK_NULL;
		}

		// cbCurrentCard一定不为0 !!!!!!!!!
		if (cur_card == 0)
			return GameConstants.WIK_NULL;

		// 设置变量
		List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		chiHuRight.set_empty();

		// 构造扑克
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		// 插入扑克
		cbCardIndexTemp[_logic.switch_to_card_index(cur_card)]++;

		boolean hu = false;// 大胡标记
		// 七小对牌 豪华七小对--可能是不能胡的牌型
		long qxd = _logic.is_qi_xiao_dui(cards_index, weaveItem, weaveCount, cur_card);
		if (qxd != GameConstants.WIK_NULL) {
			chiHuRight.opr_or(qxd);
			hu = true;
			if (_logic.is_qing_yi_se(cards_index, weaveItem, weaveCount, cur_card)) {
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_QING_YI_SE);
			}
			return GameConstants.WIK_CHI_HU;// 七小对不算门清 可以有清一色--不与其他大胡组合 直接返回
		}

		// 分析扑克
		boolean bValue = _logic.analyse_card(cbCardIndexTemp, weaveItem, weaveCount, analyseItemArray, false);
		if (card_type == GameConstants.HU_CARD_TYPE_ZIMO) {
			chiHuRight.opr_or(GameConstants.CHR_ZI_MO);
		} else {
			chiHuRight.opr_or(GameConstants.CHR_SHU_FAN);
		}

		if (hu == false && bValue == false) {
			chiHuRight.set_empty();
			return GameConstants.WIK_NULL;
		}

		/*
		 * // 特殊番型
		 */

		// // 将将胡
		// if (_logic.is_jiangjiang_hu(cards_index, weaveItem, weaveCount,
		// cur_card)) {
		// //chiHuRight.opr_or(MJGameConstants.CHR_HUNAN_JIANGJIANG_HU);
		// hu = true;
		// }

		// 全求人
		// if (_logic.is_dan_diao(cards_index, cur_card)) {// weaveCount == 4&&
		// //chiHuRight.opr_or(MJGameConstants.CHR_QUAN_QIU_REN);
		// hu = true;
		// }

		// 清一色牌
		if (_logic.is_qing_yi_se(cards_index, weaveItem, weaveCount, cur_card)) {
			chiHuRight.opr_or(GameConstants.CHR_HUNAN_QING_YI_SE);
			hu = true;

			// if(weaveCount==0) {//特殊牌型 门清可以乱将
			// chiHuRight.opr_or(MJGameConstants.CHR_MEN_QING);
			// }
		}

		// 牌型分析
		boolean isPenghu = false;
		for (int i = 0; i < analyseItemArray.size(); i++) {
			// 变量定义
			AnalyseItem analyseItem = analyseItemArray.get(i);
			// 碰碰和
			if (_logic.is_pengpeng_hu(analyseItem)) {
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_PENGPENG_HU);
				hu = true;
				isPenghu = true;

				// if(weaveCount==0) {//特殊牌型 门清可以乱将
				// chiHuRight.opr_or(MJGameConstants.CHR_MEN_QING);
				// }
			}

			int cbCardValue = _logic.get_card_value(analyseItem.cbCardEye);
			if (cbCardValue == 2 || cbCardValue == 5 || cbCardValue == 8) {
				if (isPenghu) {
					chiHuRight.opr_or(GameConstants.CHR_HUNAN_258_JIANG);// 2 5
																			// 8碰碰胡
				}
			}
		}

		if (hu == true) {
			if (weaveCount == 0) {// 有大胡 门清可以乱将
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_MEN_QING);
			}
			// 有大胡
			return GameConstants.WIK_CHI_HU;
		}

		// 胡牌分析 有没有258
		for (int i = 0; i < analyseItemArray.size(); i++) {
			// 变量定义
			AnalyseItem pAnalyseItem = analyseItemArray.get(i);
			int cbCardValue = _logic.get_card_value(pAnalyseItem.cbCardEye);
			if (cbCardValue != 2 && cbCardValue != 5 && cbCardValue != 8) {
				continue;
			}
			hu = true;
			if (weaveCount == 0) {
				chiHuRight.opr_or(GameConstants.CHR_HUNAN_MEN_QING);
			}
			return GameConstants.WIK_CHI_HU;
		}

		chiHuRight.set_empty();
		return GameConstants.WIK_NULL;
	}

	// 玩家出牌的动作检测
	public boolean estimate_player_out_card_respond_cs(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			if (_playerStatus[i].lock_huan_zhang() == false) {
				//// 碰牌判断
				action = _logic.check_peng(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(action);
					playerStatus.add_peng(card, seat_index);
					bAroseAction = true;
				}
			}
			// 杠牌判断 如果剩余牌大于1，是否有杠,剩一张为海底
			if (GRR._left_card_count > 1) {
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(GameConstants.WIK_BU_ZHNAG);
					playerStatus.add_bu_zhang(card, seat_index, 1);// 加上补张

					// 剩一张为海底
					if (GRR._left_card_count > 2) {
						// 把可以杠的这张牌去掉。看是不是听牌
						// int bu_index = _logic.switch_to_card_index(card);
						// int save_count = GRR._cards_index[i][bu_index];
						// GRR._cards_index[i][bu_index]=0;

						// boolean is_ting =
						// is_cs_ting_card(GRR._cards_index[i],
						// GRR._weave_items[i], GRR._weave_count[i]);
						boolean is_ting = false;
						// 把牌加回来
						// GRR._cards_index[i][bu_index]=save_count;
						boolean can_gang = false;
						if (is_ting == true) {
							can_gang = true;
						} else {
							// 把可以杠的这张牌去掉。看是不是听牌
							int bu_index = _logic.switch_to_card_index(card);
							int save_count = GRR._cards_index[i][bu_index];
							GRR._cards_index[i][bu_index] = 0;

							int cbWeaveIndex = GRR._weave_count[i];

							GRR._weave_items[i][cbWeaveIndex].public_card = 0;
							GRR._weave_items[i][cbWeaveIndex].center_card = card;
							GRR._weave_items[i][cbWeaveIndex].weave_kind = GameConstants.WIK_GANG;// 接杠
							GRR._weave_items[i][cbWeaveIndex].provide_player = seat_index;
							GRR._weave_count[i]++;

							can_gang = this.is_cs_ting_card(GRR._cards_index[i], GRR._weave_items[i],
									GRR._weave_count[i]);

							// 把牌加回来
							GRR._cards_index[i][bu_index] = save_count;
							GRR._weave_count[i] = cbWeaveIndex;

						}

						if (can_gang == true) {
							playerStatus.add_action(GameConstants.WIK_GANG);
							playerStatus.add_gang(card, seat_index, 1);// 加上杠
							bAroseAction = true;
						}
					}
				}
			}
			// 如果是自摸胡
			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_PAOHU);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		int chi_seat_index = (seat_index + 1) % GameConstants.GAME_PLAYER;
		// 长沙麻将吃操作 转转麻将不能吃
		if (_playerStatus[chi_seat_index].lock_huan_zhang() == false) {
			// 这里可能有问题 应该是 |=
			action = _logic.check_chi(GRR._cards_index[chi_seat_index], card);
			if ((action & GameConstants.WIK_LEFT) != 0) {
				_playerStatus[chi_seat_index].add_action(GameConstants.WIK_LEFT);
				_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_LEFT, seat_index);
			}
			if ((action & GameConstants.WIK_CENTER) != 0) {
				_playerStatus[chi_seat_index].add_action(GameConstants.WIK_CENTER);
				_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_CENTER, seat_index);
			}
			if ((action & GameConstants.WIK_RIGHT) != 0) {
				_playerStatus[chi_seat_index].add_action(GameConstants.WIK_RIGHT);
				_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_RIGHT, seat_index);
			}

			// 结果判断
			if (_playerStatus[chi_seat_index].has_action()) {
				bAroseAction = true;
			}
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;
		} else {
			return false;
		}
		return true;

	}

	/***
	 * 玩家出牌的动作检测--玩家出牌 响应判断,是否有吃碰杠补胡
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_player_out_card_respond_zhuzhou(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			if (_playerStatus[i].lock_huan_zhang() == false) {
				//// 碰牌判断
				action = _logic.check_peng(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(action);
					playerStatus.add_peng(card, seat_index);
					bAroseAction = true;
				}
			}
			// 杠牌判断 如果剩余牌大于1，是否有杠,剩一张为海底
			if (GRR._left_card_count > 1) {
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				if (action != 0) {
					// playerStatus.add_action(MJGameConstants.WIK_BU_ZHNAG);
					// playerStatus.add_bu_zhang(card, seat_index, 1);// 加上补张
					// 剩一张为海底
					// if (GRR._left_card_count > 2) {
					// 把可以杠的这张牌去掉。看是不是听牌
					int bu_index = _logic.switch_to_card_index(card);
					int save_count = GRR._cards_index[i][bu_index];
					GRR._cards_index[i][bu_index] = 0;

					boolean is_ting = is_zhuzhou_ting_card(GRR._cards_index[i], GRR._weave_items[i],
							GRR._weave_count[i]);

					// 把牌加回来
					GRR._cards_index[i][bu_index] = save_count;

					if (is_ting == true) {
						playerStatus.add_action(GameConstants.WIK_GANG);
						playerStatus.add_gang(card, seat_index, 1);// 加上杠
						bAroseAction = true;

					}
					// }
				}
			}
			// 如果是自摸胡
			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_PAOHU);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		int chi_seat_index = (seat_index + 1) % GameConstants.GAME_PLAYER;
		// 长沙麻将吃操作 转转麻将不能吃
		if (_playerStatus[chi_seat_index].lock_huan_zhang() == false) {
			// 这里可能有问题 应该是 |=
			action = _logic.check_chi(GRR._cards_index[chi_seat_index], card);
			if ((action & GameConstants.WIK_LEFT) != 0) {
				_playerStatus[chi_seat_index].add_action(GameConstants.WIK_LEFT);
				_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_LEFT, seat_index);
			}
			if ((action & GameConstants.WIK_CENTER) != 0) {
				_playerStatus[chi_seat_index].add_action(GameConstants.WIK_CENTER);
				_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_CENTER, seat_index);
			}
			if ((action & GameConstants.WIK_RIGHT) != 0) {
				_playerStatus[chi_seat_index].add_action(GameConstants.WIK_RIGHT);
				_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_RIGHT, seat_index);
			}

			// 结果判断
			if (_playerStatus[chi_seat_index].has_action()) {
				bAroseAction = true;
			}
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;
		} else {
			return false;
		}
		return true;

	}

	// 玩家出牌的动作检测
	public boolean estimate_player_out_card_respond_henan(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		// int llcard = get_niao_card_num(true, 0);
		int llcard = 0;
		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {// 带混玩法 剩余14张 结算
			llcard = GameConstants.CARD_COUNT_LEFT_HUANGZHUANG;
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[i], card);
			if (action != 0 && has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN) && _logic.is_magic_card(card)) {// 鬼牌不能碰
				action = 0;
			}
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);
				bAroseAction = true;
			}
			if (GRR._left_card_count > llcard) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				if (action != 0 && has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN) && _logic.is_magic_card(card)) {// 鬼牌不能杠
					action = 0;
				}
				if (action != 0) {
					playerStatus.add_action(GameConstants.WIK_GANG);
					playerStatus.add_gang(card, seat_index, 1);// 加上杠
					bAroseAction = true;
				}
			}

			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_henan(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_PAOHU);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	// 玩家出牌的动作检测
	public boolean estimate_player_out_card_respond_hz(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		int llcard = get_niao_card_num(true, 0);
		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[i], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);
				bAroseAction = true;
			}
			if (GRR._left_card_count > llcard) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(GameConstants.WIK_GANG);
					playerStatus.add_gang(card, seat_index, 1);// 加上杠
					bAroseAction = true;
				}
			}

			// 红中不能胡
			if (card != _logic.switch_to_card_data(_logic.get_magic_card_index(0))) {
				// 可以胡的情况 判断
				if (_playerStatus[i].is_chi_hu_round()) {
					// 吃胡判断
					ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
					chr.set_empty();
					int cbWeaveCount = GRR._weave_count[i];
					action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
							GameConstants.HU_CARD_TYPE_PAOHU);

					// 结果判断
					if (action != 0) {
						_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
						_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
						bAroseAction = true;
					}
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	/**
	 * 河南红中麻将出牌检测
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_player_out_card_respond_hnhz(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		int llcard = get_niao_card_num(true, 0);
		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[i], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);
				bAroseAction = true;
			}
			if (GRR._left_card_count > llcard) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(GameConstants.WIK_GANG);
					playerStatus.add_gang(card, seat_index, 1);// 加上杠
					bAroseAction = true;
				}
			}

			// 红中不能胡
			if (card != _logic.switch_to_card_data(_logic.get_magic_card_index(0))) {
				// 可以胡的情况 判断
				if (_playerStatus[i].is_chi_hu_round()) {
					// 吃胡判断
					ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
					chr.set_empty();
					int cbWeaveCount = GRR._weave_count[i];
					action = this.analyse_chi_hu_card_hnhz(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card,
							chr, GameConstants.HU_CARD_TYPE_PAOHU);

					// 结果判断
					if (action != 0) {
						_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
						_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
						bAroseAction = true;
					}
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	// 玩家出牌的动作检测
	public boolean estimate_player_out_card_respond_zz(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[i], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);
				bAroseAction = true;
			}

			if (GRR._left_card_count > 0) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(GameConstants.WIK_GANG);
					playerStatus.add_gang(card, seat_index, 1);// 加上杠
					bAroseAction = true;
				}
			}

			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_PAOHU);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	/**
	 * 仙桃晃晃 //玩家出牌的动作检测
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_player_out_card_respond_xthh(int seat_index, int card, int type) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			action = _logic.check_peng_xthh(GRR._cards_index[i], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);
				bAroseAction = true;
			}

			if (GRR._left_card_count > 0) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card_xthh(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(action);
					playerStatus.add_xiao(card, action, seat_index, 1);// 加上笑
					bAroseAction = true;
				}
			}

			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr, type);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	/**
	 * 安阳麻将出牌检测
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_player_out_card_respond_ay(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			if (playerStatus.is_bao_ting() == false) {
				action = _logic.check_peng(GRR._cards_index[i], card);
				if (action != 0) {
					playerStatus.add_action(action);
					playerStatus.add_peng(card, seat_index);
					bAroseAction = true;
				}
			}

			if (GRR._left_card_count > 0) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				// 有杠而且杠完不换章
				if (action != 0) {
					if ((playerStatus.is_bao_ting() == false)) {
						playerStatus.add_action(GameConstants.WIK_GANG);
						playerStatus.add_gang(card, i, 1);// 加上杠
						bAroseAction = true;
					} else {
						if (check_gang_huan_zhang(GameConstants.GAME_TYPE_HENAN_AY, i, card) == false) {
							playerStatus.add_action(GameConstants.WIK_GANG);
							playerStatus.add_gang(card, i, 1);// 加上杠
							bAroseAction = true;
						}

					}
				}
			}

			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				if (_playerStatus[seat_index].is_bao_ting() == false || _playerStatus[i].is_bao_ting() == false) {
					continue;
				}
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_ay(i, GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_PAOHU);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合

					bAroseAction = true;
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	/**
	 * 林州麻将出牌检测
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_player_out_card_respond_lz(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			_playerStatus[i].clean_action();
			_playerStatus[i].clean_weave();
		}

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];

			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[i], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);
				bAroseAction = true;
			}

			if (GRR._left_card_count > 0) {
				// 杠牌判断
				action = _logic.estimate_gang_card_out_card(GRR._cards_index[i], card);
				// 有杠而且杠完不换章
				if (action != 0) {
					playerStatus.add_action(GameConstants.WIK_GANG);
					playerStatus.add_gang(card, i, 1);// 加上杠
					bAroseAction = true;
				}
			}

			// 可以胡的情况 判断
			if (_playerStatus[i].is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_lz(i, GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_PAOHU);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合

					bAroseAction = true;
				}
			}
			// }
		}

		if (bAroseAction) {
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
			_provide_player = seat_index;

		} else {

			return false;
		}

		return true;

	}

	/**
	 * //玩家出牌的动作检测
	 * 
	 * private boolean estimate_player_dispath_respond(int seat_index, int
	 * card){ // 变量定义 boolean bAroseAction = false;// 出现(是否)有
	 * 
	 * // 用户状态 for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	 * 
	 * _playerStatus[i].clean_action(); _playerStatus[i].clean_weave(); }
	 * 
	 * PlayerStatus playerStatus = null;
	 * 
	 * int action = MJGameConstants.WIK_NULL;
	 * 
	 * // 动作判断 for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) { // 用户过滤
	 * if (seat_index == i) continue;
	 * 
	 * playerStatus = _playerStatus[i]; //// 碰牌判断 action =
	 * _logic.check_peng(GRR._cards_index[i], card); if(action!=0){
	 * playerStatus.add_action(action); playerStatus.add_peng(card,seat_index);
	 * }
	 * 
	 * 
	 * // 杠牌判断 如果剩余牌大于1，是否有杠 if (GRR._left_card_count > 0) { action =
	 * _logic.estimate_gang_card(GRR._cards_index[i], card);
	 * 
	 * if(action!=0){ playerStatus.add_action(MJGameConstants.WIK_BU_ZHNAG);
	 * playerStatus.add_bu_zhang(card, seat_index, 1);//加上补涨
	 * 
	 * //长沙麻将判断是不是听牌 if(is_mj_type(MJGameConstants.GAME_TYPE_CS) &&
	 * action==MJGameConstants.WIK_NULL){ boolean is_ting =
	 * _logic.is_cs_ting_card(GRR._cards_index[i], GRR._weave_items[i],
	 * GRR._weave_count[i]);
	 * 
	 * if(is_ting==true){ playerStatus.add_action(MJGameConstants.WIK_GANG);
	 * playerStatus.add_gang(card, seat_index, 1);//加上杠
	 * 
	 * } } } }
	 * 
	 * // 吃胡判断 ChiHuRight chr =
	 * GRR._chi_hu_rights[i];//playerStatus._chiHuRight; chr.set_empty(); int
	 * cbWeaveCount = GRR._weave_count[i]; action =
	 * analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i],
	 * cbWeaveCount, card,chr );
	 * 
	 * // 结果判断 if (action!=0){
	 * _playerStatus[i].add_action(MJGameConstants.WIK_CHI_HU);
	 * _playerStatus[i].add_chi_hu(card,seat_index);//吃胡的组合 bAroseAction = true;
	 * } }
	 * 
	 * int chi_seat_index = (seat_index+1)%MJGameConstants.GAME_PLAYER; //
	 * 长沙麻将吃操作 转转麻将不能吃 if (_game_type_index == MJGameConstants.GAME_TYPE_CS) {
	 * // 这里可能有问题 应该是 |= action =
	 * _logic.check_chi(GRR._cards_index[chi_seat_index], card);
	 * if((action&MJGameConstants.WIK_LEFT) !=0){
	 * _playerStatus[chi_seat_index].add_action(MJGameConstants.WIK_LEFT);
	 * _playerStatus[chi_seat_index].add_chi(card,MJGameConstants.WIK_LEFT,seat_index);
	 * } if((action&MJGameConstants.WIK_CENTER) !=0){
	 * _playerStatus[chi_seat_index].add_action(MJGameConstants.WIK_CENTER);
	 * _playerStatus[chi_seat_index].add_chi(card,MJGameConstants.WIK_CENTER,seat_index);
	 * } if((action&MJGameConstants.WIK_RIGHT) !=0){
	 * _playerStatus[chi_seat_index].add_action(MJGameConstants.WIK_RIGHT);
	 * _playerStatus[chi_seat_index].add_chi(card,MJGameConstants.WIK_RIGHT,seat_index);
	 * }
	 * 
	 * // 结果判断 if (_playerStatus[chi_seat_index].has_action()){ bAroseAction =
	 * true; } }
	 * 
	 * if(bAroseAction){ _resume_player = _current_player;// 保存当前轮到的玩家
	 * _current_player = MJGameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
	 * _provide_player = seat_index;
	 * 
	 * for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) { playerStatus =
	 * _playerStatus[i]; if(playerStatus.has_action()){
	 * playerStatus.set_status(MJGameConstants.Player_Status_OPR_CARD);//操作状态
	 * 
	 * 
	 * //playerStatus._provide_card = card;
	 * 
	 * this.operate_player_action(i, false); } } }else{
	 * 
	 * return false; }
	 * 
	 * 
	 * return true;
	 * 
	 * }
	 **/

	// 检查杠牌,有没有胡的
	public boolean estimate_gang_respond_zhuzhou(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			if (playerStatus.is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_QIANGGANG);

				// 结果判断
				if (action != 0) {
					// chr.opr_or(MJGameConstants.CHR_QIANG_GANG_HU);// 抢杠胡
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	/***
	 * 检查杠牌,有没有胡的 检查所有人
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_gang_respond_henan(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			if (playerStatus.is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_henan(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_QIANGGANG);

				// 结果判断
				if (action != 0) {
					chr.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);// 抢杠胡
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	/***
	 * 河南红中麻将检查杠牌,有没有胡的 检查所有人
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_gang_respond_hnhz(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			if (playerStatus.is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_hnhz(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_QIANGGANG);

				// 结果判断
				if (action != 0) {
					chr.opr_or(GameConstants.CHR_HENAN_QIANG_GANG_HU);// 抢杠胡
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	/***
	 * 湖南麻将杠牌检测
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_gang_respond(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			if (playerStatus.is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_QIANGGANG);

				// 结果判断
				if (action != 0) {
					chr.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);// 抢杠胡
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	// 检查杠牌,有没有胡的
	public boolean estimate_gang_respond_cs(int seat_index, int card, int _action) {
		// 变量定义
		boolean isGang = _action == GameConstants.WIK_GANG ? true : false;// 补张
																			// 不算抢杠胡

		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			// if(playerStatus.is_chi_hu_round()){
			// 吃胡判断
			ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
			chr.set_empty();
			int cbWeaveCount = GRR._weave_count[i];
			action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
					GameConstants.HU_CARD_TYPE_QIANGGANG);

			// 结果判断
			if (action != 0) {
				if (isGang) {
					chr.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);// 抢杠胡
				}
				_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
				_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
				bAroseAction = true;
			}
			// }
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	/**
	 * 仙桃晃晃 //检查杠牌,有没有胡的
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_gang_xthh_respond(int seat_index, int card) {
		if (GRR._piao_lai_count > 0) {
			return false;
		}

		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			// 有癞子不能抢杠胡
			if (GRR._cards_index[i][_logic.get_magic_card_index(0)] > 0) {
				continue;
			}

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			// if(playerStatus.is_chi_hu_round()){
			// 吃胡判断
			ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
			chr.set_empty();
			int cbWeaveCount = GRR._weave_count[i];
			action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
					GameConstants.HU_CARD_TYPE_QIANGGANG);

			// 结果判断
			if (action != 0) {
				_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
				_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
				bAroseAction = true;
			}
			// }
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	/**
	 * 安阳麻将，检查这个杠有没有胡
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_gang_ay_respond(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;
			if (_playerStatus[seat_index].is_bao_ting() == false || _playerStatus[i].is_bao_ting() == false) {
				continue;
			}

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			if (playerStatus.is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_ay(i, GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_QIANGGANG);

				// 结果判断
				if (action != 0) {
					chr.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);// 抢杠胡
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	/**
	 * 林州麻将，检查这个杠有没有胡
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean estimate_gang_lz_respond(int seat_index, int card) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		// 动作判断
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 用户过滤
			if (seat_index == i)
				continue;

			playerStatus = _playerStatus[i];
			// 可以胡的情况 判断
			if (playerStatus.is_chi_hu_round()) {
				// 吃胡判断
				ChiHuRight chr = GRR._chi_hu_rights[i];// playerStatus._chiHuRight;
				chr.set_empty();
				int cbWeaveCount = GRR._weave_count[i];
				action = analyse_chi_hu_card_lz(i, GRR._cards_index[i], GRR._weave_items[i], cbWeaveCount, card, chr,
						GameConstants.HU_CARD_TYPE_QIANGGANG);

				// 结果判断
				if (action != 0) {
					_playerStatus[i].add_action(GameConstants.WIK_CHI_HU);
					_playerStatus[i].add_chi_hu(card, seat_index);// 吃胡的组合
					bAroseAction = true;
				}
			}
		}

		if (bAroseAction == true) {
			_provide_player = seat_index;// 谁打的牌
			_provide_card = card;// 打的什么牌
			_resume_player = _current_player;// 保存当前轮到的玩家
			_current_player = GameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
		}

		return bAroseAction;
	}

	// 检查长沙麻将,杠牌
	public boolean estimate_gang_cs_respond(int seat_index, int provider, int card, boolean d, boolean check_chi) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		playerStatus = _playerStatus[seat_index];
		// playerStatus.clean_action();

		if (playerStatus.lock_huan_zhang() == false) {
			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[seat_index], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);

				bAroseAction = true;
			}

		}

		// 杠牌判断 如果剩余牌大于1，是否有杠
		if (GRR._left_card_count > 1) {
			if (GRR._cards_index[seat_index][_logic.switch_to_card_index(card)] == 3) {
				playerStatus.add_action(GameConstants.WIK_BU_ZHNAG);
				playerStatus.add_bu_zhang(card, provider, 1);// 加上补涨

				if (GRR._left_card_count > 2) {
					// int bu_index = _logic.switch_to_card_index(card);
					// int save_count = GRR._cards_index[seat_index][bu_index];
					// GRR._cards_index[seat_index][bu_index]=0;
					// 长沙麻将判断是不是听牌
					// boolean is_ting =
					// is_cs_ting_card(GRR._cards_index[seat_index],
					// GRR._weave_items[seat_index],
					// GRR._weave_count[seat_index]);
					boolean is_ting = false;
					// 把牌加回来
					// GRR._cards_index[seat_index][bu_index] = save_count;
					// 把牌加回来
					// GRR._cards_index[i][bu_index]=save_count;
					boolean can_gang = false;
					if (is_ting == true) {
						can_gang = true;
					} else {
						// 把可以杠的这张牌去掉。看是不是听牌
						int bu_index = _logic.switch_to_card_index(card);
						int save_count = GRR._cards_index[seat_index][bu_index];
						GRR._cards_index[seat_index][bu_index] = 0;

						int cbWeaveIndex = GRR._weave_count[seat_index];

						GRR._weave_items[seat_index][cbWeaveIndex].public_card = 1;
						GRR._weave_items[seat_index][cbWeaveIndex].center_card = card;
						GRR._weave_items[seat_index][cbWeaveIndex].weave_kind = GameConstants.WIK_GANG;// 接杠
						GRR._weave_items[seat_index][cbWeaveIndex].provide_player = provider;
						GRR._weave_count[seat_index]++;

						can_gang = this.is_cs_ting_card(GRR._cards_index[seat_index], GRR._weave_items[seat_index],
								GRR._weave_count[seat_index]);

						GRR._weave_count[seat_index] = cbWeaveIndex;
						GRR._cards_index[seat_index][bu_index] = save_count;
					}

					if (can_gang == true) {

						playerStatus.add_action(GameConstants.WIK_GANG);
						playerStatus.add_gang(card, provider, 1);// 加上杠

					}
				}

				bAroseAction = true;
			}

		}

		// 吃胡判断
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];// playerStatus._chiHuRight;
		// chr.set_empty();
		int cbWeaveCount = GRR._weave_count[seat_index];
		action = analyse_chi_hu_card(GRR._cards_index[seat_index], GRR._weave_items[seat_index], cbWeaveCount, card,
				chr, GameConstants.HU_CARD_TYPE_PAOHU);

		// 结果判断
		if (action != 0) {
			if (d) {
				chr.opr_or(GameConstants.CHR_HUNAN_SHUANG_GANG_SHANG_PAO);
			} else {
				chr.opr_or(GameConstants.CHR_HUNAN_GANG_SHANG_PAO);
			}
			if (_playerStatus[seat_index].has_chi_hu() == false) {
				_playerStatus[seat_index].add_action(GameConstants.WIK_CHI_HU);
				_playerStatus[seat_index].add_chi_hu(card, provider);// 吃胡的组合
			}

			bAroseAction = true;
		}

		if (check_chi) {
			int chi_seat_index = (provider + 1) % GameConstants.GAME_PLAYER;
			if (_playerStatus[chi_seat_index].lock_huan_zhang() == false) {
				// 长沙麻将吃操作 转转麻将不能吃
				action = _logic.check_chi(GRR._cards_index[chi_seat_index], card);
				if ((action & GameConstants.WIK_LEFT) != 0) {
					_playerStatus[chi_seat_index].add_action(GameConstants.WIK_LEFT);
					_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_LEFT, seat_index);
				}
				if ((action & GameConstants.WIK_CENTER) != 0) {
					_playerStatus[chi_seat_index].add_action(GameConstants.WIK_CENTER);
					_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_CENTER, seat_index);
				}
				if ((action & GameConstants.WIK_RIGHT) != 0) {
					_playerStatus[chi_seat_index].add_action(GameConstants.WIK_RIGHT);
					_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_RIGHT, seat_index);
				}

				// 结果判断
				if (_playerStatus[chi_seat_index].has_action()) {
					bAroseAction = true;
				}
			}

		}

		return bAroseAction;
	}

	/**
	 * 检查株洲麻将,杠牌 --检查其它玩家对这张牌的 吃碰杆胡
	 * 
	 * @param seat_index
	 * @param provider
	 * @param card
	 * @param d
	 * @param check_chi
	 *            -是否检查吃
	 * @return
	 */
	public boolean estimate_gang_zhuzhou_respond(int seat_index, int provider, int card, boolean d, boolean check_chi) {
		// 变量定义
		boolean bAroseAction = false;// 出现(是否)有

		PlayerStatus playerStatus = null;

		int action = GameConstants.WIK_NULL;

		playerStatus = _playerStatus[seat_index];
		if (playerStatus.lock_huan_zhang() == false) {
			//// 碰牌判断
			action = _logic.check_peng(GRR._cards_index[seat_index], card);
			if (action != 0) {
				playerStatus.add_action(action);
				playerStatus.add_peng(card, seat_index);

				bAroseAction = true;
			}

		}

		// 杠牌判断 如果剩余牌大于1，是否有杠
		if (GRR._left_card_count > 1) {
			if (GRR._cards_index[seat_index][_logic.switch_to_card_index(card)] == 3) {
				// playerStatus.add_action(MJGameConstants.WIK_BU_ZHNAG);
				// playerStatus.add_bu_zhang(card, provider, 1);//加上补涨

				// if(GRR._left_card_count > 2){
				int bu_index = _logic.switch_to_card_index(card);
				int save_count = GRR._cards_index[seat_index][bu_index];
				GRR._cards_index[seat_index][bu_index] = 0;
				// 长沙麻将判断是不是听牌
				boolean is_ting = is_zhuzhou_ting_card(GRR._cards_index[seat_index], GRR._weave_items[seat_index],
						GRR._weave_count[seat_index]);

				// 把牌加回来
				GRR._cards_index[seat_index][bu_index] = save_count;

				if (is_ting == true) {

					playerStatus.add_action(GameConstants.WIK_GANG);
					playerStatus.add_gang(card, provider, 1);// 加上杠

				}
				// }

				bAroseAction = true;
			}

		}

		// 吃胡判断
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];// playerStatus._chiHuRight;
		chr.set_empty();
		int cbWeaveCount = GRR._weave_count[seat_index];
		action = analyse_chi_hu_card(GRR._cards_index[seat_index], GRR._weave_items[seat_index], cbWeaveCount, card,
				chr, GameConstants.HU_CARD_TYPE_PAOHU);

		// 结果判断
		if (action != 0) {
			_playerStatus[seat_index].add_action(GameConstants.WIK_CHI_HU);
			if (d) {
				chr.opr_or(GameConstants.CHR_HUNAN_SHUANG_GANG_SHANG_PAO);
			} else {
				chr.opr_or(GameConstants.CHR_HUNAN_GANG_SHANG_PAO);
			}
			_playerStatus[seat_index].add_chi_hu(card, provider);// 吃胡的组合
			bAroseAction = true;
		}

		if (check_chi) {
			int chi_seat_index = (provider + 1) % GameConstants.GAME_PLAYER;
			if (_playerStatus[chi_seat_index].lock_huan_zhang() == false) {
				// 长沙麻将吃操作 转转麻将不能吃
				action = _logic.check_chi(GRR._cards_index[chi_seat_index], card);
				if ((action & GameConstants.WIK_LEFT) != 0) {
					_playerStatus[chi_seat_index].add_action(GameConstants.WIK_LEFT);
					_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_LEFT, seat_index);
				}
				if ((action & GameConstants.WIK_CENTER) != 0) {
					_playerStatus[chi_seat_index].add_action(GameConstants.WIK_CENTER);
					_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_CENTER, seat_index);
				}
				if ((action & GameConstants.WIK_RIGHT) != 0) {
					_playerStatus[chi_seat_index].add_action(GameConstants.WIK_RIGHT);
					_playerStatus[chi_seat_index].add_chi(card, GameConstants.WIK_RIGHT, seat_index);
				}

				// 结果判断
				if (_playerStatus[chi_seat_index].has_action()) {
					bAroseAction = true;
				}
			}

		}

		return bAroseAction;
	}

	// 响应判断 是否有碰,如果剩余牌大于1，是否有杠,是否胡牌，下家是否有吃
	// private boolean estimate_player_respond(int seat_index, int card, int
	// estimatKind) {
	// // 变量定义
	// boolean bAroseAction = false;// 出现(是否)有
	//
	// // 用户状态
	// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	//// _response[i] = false;// 是否做出反应
	//// _player_action[i] = 0;// 是否有操作
	//// _perform_action[i] = 0;// 操作了什么
	// _playerStatus[i].clean_action();
	// }
	//
	// int action = MJGameConstants.WIK_NULL;
	//
	// // 动作判断
	// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	// // 用户过滤
	// if (seat_index == i)
	// continue;
	//
	//
	// // 出牌类型
	// if (estimatKind == EstimatKind.EstimatKind_OutCard) {
	// //// 碰牌判断
	// action = _logic.check_peng(GRR._cards_index[i], card);
	//
	// _playerStatus[i].add_action(action);
	//
	// // 杠牌判断 如果剩余牌大于1，是否有杠
	// if (GRR._left_card_count > 0) {
	// action = _logic.estimate_gang_card(GRR._cards_index[i], card);
	// _playerStatus[i].add_action(action);
	// }
	// }
	//
	// // 吃胡判断
	// ChiHuRight chr = GRR._chi_hu_rights[i];
	// chr.set_empty();
	// int cbWeaveCount = GRR._weave_count[i];
	// action = analyse_chi_hu_card(GRR._cards_index[i], GRR._weave_items[i],
	// cbWeaveCount, card,chr );
	//
	// // 结果判断
	// if (_playerStatus[i].has_action()){
	// bAroseAction = true;
	// }
	// }
	//
	// // 长沙麻将吃操作 转转麻将不能吃
	// if (_game_type_index == MJGameConstants.GAME_TYPE_CS && seat_index !=
	// _current_player)
	// {
	// // 这里可能有问题 应该是 |=
	// action = _logic.check_chi(GRR._cards_index[_current_player], card);
	// _playerStatus[_current_player].add_action(action);
	//
	// // 结果判断
	// if (_playerStatus[_current_player].has_action()){
	// bAroseAction = true;
	// }
	// }
	// // 结果处理
	// if (bAroseAction == true) {
	// // 设置变量
	// _provide_player = seat_index;// 谁打的牌
	// _provide_card = card;// 打的什么牌
	// _resume_player = _current_player;// 保存当前轮到的玩家
	// _current_player = MJGameConstants.INVALID_SEAT;// 有需要操作的玩家。当前玩家为空
	//
	// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	// if (_player_action[i] != MJGameConstants.WIK_NULL) {
	// _player_status[i] = MJGameConstants.Player_Status_OPR_CARD;// 操作状态
	// }
	// }
	//
	// // 发送提示(优先级判断呢？)
	// this.notify_operate();
	// return true;
	// }
	//
	// return false;
	// }

	/****
	 * 
	 * 
	 * 
	 * 消息处理
	 * 
	 * 
	 ****/
	/**
	 * 创建房间
	 * 
	 * @return
	 */
	public boolean handler_create_room(Player player) {
		// c成功
		get_players()[0] = player;
		player.set_seat_index(0);

		// 发送进入房间
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_ENTER_ROOM);
		roomResponse.setGameStatus(_game_status);
		//
		this.load_room_info_data(roomResponse);
		this.load_player_info_data(roomResponse);

		return send_response_to_player(player.get_seat_index(), roomResponse);
	}

	// 玩家进入房间
	@Override
	public boolean handler_enter_room(Player player) {
		int seat_index = GameConstants.INVALID_SEAT;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			if (get_players()[i] == null) {
				get_players()[i] = player;
				seat_index = i;
				break;
			}
		}
		if (seat_index == GameConstants.INVALID_SEAT) {
			return false;
		}
		player.set_seat_index(seat_index);
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_ENTER_ROOM);
		roomResponse.setGameStatus(_game_status);

		this.load_room_info_data(roomResponse);
		this.load_player_info_data(roomResponse);
		//
		send_response_to_player(player.get_seat_index(), roomResponse);

		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 进入房间
		send_response_to_other(player.get_seat_index(), roomResponse);

		return true;
	}

	// 玩家进入房间
	@Override
	public boolean handler_reconnect_room(Player player) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_ENTER_ROOM);
		roomResponse.setGameStatus(_game_status);

		//
		this.load_player_info_data(roomResponse);
		this.load_room_info_data(roomResponse);

		send_response_to_player(player.get_seat_index(), roomResponse);

		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 进入房间
		send_response_to_other(player.get_seat_index(), roomResponse);

		return true;
	}

	@Override
	public boolean handler_player_ready(int seat_index) {

		if (this.get_players()[seat_index] == null) {
			return false;
		}
		_player_ready[seat_index] = 1;
		if (GameConstants.GS_MJ_FREE != _game_status && GameConstants.GS_MJ_WAIT != _game_status) {
			return false;
		}

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_PLAYER_READY);
		roomResponse.setOperatePlayer(seat_index);
		send_response_to_room(roomResponse);

		if (this._cur_round > 0) {
			// 结束后刷新玩家
			RoomResponse.Builder roomResponse2 = RoomResponse.newBuilder();
			roomResponse2.setGameStatus(_game_status);
			roomResponse2.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
			this.load_player_info_data(roomResponse2);
			this.send_response_to_player(seat_index, roomResponse2);
		}

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			if(this.get_players()[i] == null){
				_player_ready[i] = 0;
			}
			
			if (_player_ready[i] == 0) {
				return false;
			}
		}

		return handler_game_start();
	}

	@Override
	public boolean handler_player_be_in_room(int seat_index) {
		if ((GameConstants.GS_MJ_FREE != _game_status && GameConstants.GS_MJ_WAIT != _game_status)
				&& this.get_players()[seat_index] != null) {
			// this.send_play_data(seat_index);

			if (this._handler != null)
				this._handler.handler_player_be_in_room(this, seat_index);

		}
		if (_gameRoomRecord != null) {
			if (_gameRoomRecord.request_player_seat != GameConstants.INVALID_SEAT) {
				RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
				roomResponse.setType(MsgConstants.RESPONSE_PLAYER_RELEASE);

				SysParamModel sysParamModel3007 = SysParamDict.getInstance().getSysParamModelDictionaryByGameId(1)
						.get(3007);
				int delay = 60;
				if (sysParamModel3007 != null) {
					delay = sysParamModel3007.getVal1();
				}

				roomResponse.setReleaseTime(delay);
				roomResponse.setOperateCode(0);
				roomResponse.setRequestPlayerSeat(_gameRoomRecord.request_player_seat);
				roomResponse.setLeftTime((_request_release_time - System.currentTimeMillis()) / 1000);
				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					roomResponse.addReleasePlayers(_gameRoomRecord.release_players[i]);
				}
				this.send_response_to_player(seat_index, roomResponse);
			}
		}

		return handler_player_ready(seat_index);

	}

	/**
	 * //用户出牌
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	@Override
	public boolean handler_player_out_card(int seat_index, int card) {

		if (this._handler != null) {
			this._handler.handler_player_out_card(this, seat_index, card);
		}

		return true;
	}

	/***
	 * //用户操作
	 * 
	 * @param seat_index
	 * @param operate_code
	 * @param operate_card
	 * @return
	 */
	@Override
	public boolean handler_operate_card(int seat_index, int operate_code, int operate_card) {

		if (this._handler != null) {
			this._handler.handler_operate_card(this, seat_index, operate_code, operate_card);
		}

		return true;
	}

	/**
	 * 释放
	 */
	@Override
	public boolean process_release_room() {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_FORCE_EXIT);
		this.send_response_to_room(roomResponse);

		if (_cur_round == 0) {
			Player player = null;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				player = this.get_players()[i];
				if (player == null)
					continue;
				send_error_notify(i, 2, "游戏等待超时解散");

			}
		} else {
			this.handler_game_finish(GameConstants.INVALID_SEAT, GameConstants.Game_End_RELEASE_PLAY);

		}

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			this.get_players()[i] = null;

		}

		if (_table_scheduled != null)
			_table_scheduled.cancel(false);

		// 删除房间
		PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());

		return true;

	}

	@Override
	public boolean process_flush_time() {
		long old_flush = super.getLast_flush_time();
		setLast_flush_time(System.currentTimeMillis());
		long new_flush = super.getLast_flush_time();

		// 线程安全，先不要开放，加好锁后再处理
		// //大于10分钟通知redis
		// if(new_flush - old_flush > 1000L*60*10){
		// RoomRedisModel roomRedisModel =
		// SpringService.getBean(RedisService.class).hGet(RedisConstant.ROOM,
		// super.getRoom_id()+"", RoomRedisModel.class);
		// if(roomRedisModel!=null){
		// roomRedisModel.setLast_flush_time(System.currentTimeMillis());
		// //写入redis
		// SpringService.getBean(RedisService.class).hSet(RedisConstant.ROOM,
		// super.getRoom_id()+"", roomRedisModel);
		// }
		// }

		return true;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private boolean process_xiao_hu(int seat_index, int operate_code) {
		PlayerStatus playerStatus = _playerStatus[seat_index];

		if (playerStatus.has_xiao_hu() == false) {
			this.log_player_error(seat_index, "没有小胡");
			return false;
		}

		if (operate_code != GameConstants.WIK_NULL) {
			ChiHuRight start_hu_right = GRR._start_hu_right[seat_index];

			start_hu_right.set_valid(true);// 小胡生效
			// int cbChiHuKind =
			// analyse_chi_hu_card_cs_xiaohu(GRR._cards_index[seat_index],
			// start_hu_right);
			//
			// //判断是不是有小胡
			// if(cbChiHuKind==MJGameConstants.WIK_NULL){
			// this.log_player_error(seat_index,"没有小胡");
			// return false;
			// }
			//
			int lStartHuScore = 0;

			int wFanShu = _logic.get_chi_hu_action_rank_cs(GRR._start_hu_right[seat_index]);

			lStartHuScore = wFanShu * GameConstants.CELL_SCORE;

			GRR._start_hu_score[seat_index] = lStartHuScore * 3;// 赢3个人的分数

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index)
					continue;
				GRR._lost_fan_shu[i][seat_index] = wFanShu;// 自己杠了？？？？？？？？？？？
				GRR._start_hu_score[i] -= lStartHuScore;// 输的番薯
			}

			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, start_hu_right.type_count,
					start_hu_right.type_list, start_hu_right.type_count, GameConstants.INVALID_SEAT);

			// 构造扑克
			int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
			for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
				cbCardIndexTemp[i] = GRR._cards_index[seat_index][i];
			}

			int hand_card_indexs[] = new int[GameConstants.MAX_INDEX];
			int show_card_indexs[] = new int[GameConstants.MAX_INDEX];

			for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
				hand_card_indexs[i] = GRR._cards_index[seat_index][i];
			}

			if (start_hu_right._show_all) {
				for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
					show_card_indexs[i] = GRR._cards_index[seat_index][i];
					hand_card_indexs[i] = 0;
				}
			} else {
				if (start_hu_right._index_da_si_xi != GameConstants.MAX_INDEX) {
					hand_card_indexs[start_hu_right._index_da_si_xi] = 0;
					show_card_indexs[start_hu_right._index_da_si_xi] = 4;
				}
				if ((start_hu_right._index_liul_liu_shun_1 != GameConstants.MAX_INDEX)
						&& (start_hu_right._index_liul_liu_shun_2 != GameConstants.MAX_INDEX)) {
					hand_card_indexs[start_hu_right._index_liul_liu_shun_1] = 0;
					show_card_indexs[start_hu_right._index_liul_liu_shun_1] = 3;

					hand_card_indexs[start_hu_right._index_liul_liu_shun_2] = 0;
					show_card_indexs[start_hu_right._index_liul_liu_shun_2] = 3;
				}
			}

			int cards[] = new int[GameConstants.MAX_COUNT];

			// 刷新自己手牌
			int hand_card_count = _logic.switch_to_cards_data(hand_card_indexs, cards);
			this.operate_player_cards(seat_index, hand_card_count, cards, 0, null);

			// 显示 小胡排
			hand_card_count = _logic.switch_to_cards_data(show_card_indexs, cards);
			this.operate_show_card(seat_index, GameConstants.Show_Card_XiaoHU, hand_card_count, cards,
					GameConstants.INVALID_SEAT);

		} else {
			GRR._start_hu_right[seat_index].set_empty();
		}

		// 玩家操作
		playerStatus.operate(operate_code, 0);
		//
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			playerStatus = _playerStatus[i];
			if (playerStatus.has_xiao_hu() && playerStatus.is_respone() == false) {
				return false;
			}

		}

		// 用户状态
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_playerStatus[i].clean_action();
			_playerStatus[i].clean_status();
		}

		_table_scheduled = GameSchedule.put(new XiaoHuRunnable(this.getRoom_id(), seat_index),
				GameConstants.XIAO_HU_DELAY, TimeUnit.SECONDS);

		return true;
	}

	/**
	 * 处理吃胡的玩家
	 * 
	 * @param seat_index
	 * @param operate_card
	 * @param rm
	 */
	public void process_chi_hu_player_operate(int seat_index, int operate_card, boolean rm) {
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		// filtrate_right(seat_index, chr);

		if (is_mj_type(GameConstants.GAME_TYPE_ZZ) || is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			int effect_count = chr.type_count;
			long effect_indexs[] = new long[effect_count];
			for (int i = 0; i < effect_count; i++) {
				if (chr.type_list[i] == GameConstants.CHR_SHU_FAN) {
					effect_indexs[i] = GameConstants.CHR_HU;
				} else {
					effect_indexs[i] = chr.type_list[i];
				}

			}

			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, effect_count, effect_indexs, 1,
					GameConstants.INVALID_SEAT);
		} else if (GameConstants.GAME_TYPE_CS == _game_type_index) {
			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list,
					1, GameConstants.INVALID_SEAT);
		} else if (GameConstants.GAME_TYPE_ZHUZHOU == _game_type_index) {
			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list,
					1, GameConstants.INVALID_SEAT);
		} else if (GameConstants.GAME_TYPE_HENAN_AY == _game_type_index) {
			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list,
					1, GameConstants.INVALID_SEAT);
		} else if (GameConstants.GAME_TYPE_HENAN_LZ == _game_type_index) {
			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list,
					1, GameConstants.INVALID_SEAT);
		} else if (GameConstants.GAME_TYPE_HENAN == _game_type_index) {
			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list,
					1, GameConstants.INVALID_SEAT);
		}

		// 手牌删掉
		this.operate_player_cards(seat_index, 0, null, 0, null);

		if (rm) {
			// 把摸的牌从手牌删掉,结算的时候不显示这张牌的
			GRR._cards_index[seat_index][_logic.switch_to_card_index(operate_card)]--;
		}

		// 显示胡牌
		int cards[] = new int[GameConstants.MAX_COUNT];
		int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[seat_index], cards);
		cards[hand_card_count] = operate_card + GameConstants.CARD_ESPECIAL_TYPE_HU;
		hand_card_count++;
		this.operate_show_card(seat_index, GameConstants.Show_Card_HU, hand_card_count, cards,
				GameConstants.INVALID_SEAT);

		return;
	}

	/**
	 * 河南红中麻将
	 * 
	 * @param seat_index
	 * @param operate_card
	 * @param rm
	 */
	public void process_chi_hu_player_operate_hnhz(int seat_index, int operate_card, boolean rm) {
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int effect_count = chr.type_count;
		long effect_indexs[] = new long[effect_count];
		for (int i = 0; i < effect_count; i++) {
			if (chr.type_list[i] == GameConstants.CHR_SHU_FAN) {
				effect_indexs[i] = GameConstants.CHR_HU;
			} else {
				effect_indexs[i] = chr.type_list[i];
			}

		}
		// 效果
		this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, effect_count, effect_indexs, 1,
				GameConstants.INVALID_SEAT);
		// 手牌删掉
		this.operate_player_cards(seat_index, 0, null, 0, null);

		if (rm) {
			// 把摸的牌从手牌删掉,结算的时候不显示这张牌的
			GRR._cards_index[seat_index][_logic.switch_to_card_index(operate_card)]--;
		}

		// 显示胡牌
		int cards[] = new int[GameConstants.MAX_COUNT];
		int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[seat_index], cards);
		cards[hand_card_count] = operate_card + GameConstants.CARD_ESPECIAL_TYPE_HU;
		hand_card_count++;
		this.operate_show_card(seat_index, GameConstants.Show_Card_HU, hand_card_count, cards,
				GameConstants.INVALID_SEAT);

	}

	/**
	 * 长沙
	 * 
	 * @param seat_index
	 * @param operate_card
	 * @param rm
	 */
	public void process_chi_hu_player_operate_cs(int seat_index, int operate_card[], int card_count, boolean rm) {
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		// 效果
		this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list, 1,
				GameConstants.INVALID_SEAT);

		// 手牌删掉
		this.operate_player_cards(seat_index, 0, null, 0, null);

		if (rm) {
			// 把摸的牌从手牌删掉,结算的时候不显示这张牌的
			for (int i = 0; i < card_count; i++) {
				GRR._cards_index[seat_index][_logic.switch_to_card_index(operate_card[i])]--;
			}
		}

		// 显示胡牌
		int cards[] = new int[GameConstants.MAX_INDEX];
		int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[seat_index], cards);
		for (int i = 0; i < card_count; i++) {
			cards[hand_card_count++] = operate_card[i] + GameConstants.CARD_ESPECIAL_TYPE_HU;
		}
		this.operate_show_card(seat_index, GameConstants.Show_Card_HU, hand_card_count, cards,
				GameConstants.INVALID_SEAT);

		return;
	}

	/**
	 * 双鬼 处理吃胡的玩家
	 * 
	 * @param seat_index
	 * @param operate_card
	 * @param rm
	 */
	public void process_chi_hu_player_operate_sg(int seat_index, int operate_card, boolean rm) {
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		// filtrate_right(seat_index, chr);

		if (is_mj_type(GameConstants.GAME_TYPE_ZZ) || is_mj_type(GameConstants.GAME_TYPE_HZ)
				|| is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
			int effect_count = chr.type_count;
			long effect_indexs[] = new long[effect_count];
			for (int i = 0; i < effect_count; i++) {
				if (chr.type_list[i] == GameConstants.CHR_SHU_FAN) {
					effect_indexs[i] = GameConstants.CHR_HU;
				} else {
					effect_indexs[i] = chr.type_list[i];
				}

			}

			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, effect_count, effect_indexs, 1,
					GameConstants.INVALID_SEAT);
		} else if (GameConstants.GAME_TYPE_CS == _game_type_index) {
			// 效果
			this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, chr.type_count, chr.type_list,
					1, GameConstants.INVALID_SEAT);
		}

		// 手牌删掉
		this.operate_player_cards(seat_index, 0, null, 0, null);

		if (rm) {
			// 把摸的牌从手牌删掉,结算的时候不显示这张牌的
			GRR._cards_index[seat_index][_logic.switch_to_card_index(operate_card)]--;
		}

		// 显示胡牌
		int cards[] = new int[GameConstants.MAX_COUNT];
		int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[seat_index], cards);
		for (int i = 0; i < hand_card_count; i++) {
			if (_logic.is_magic_card(cards[i])) {
				cards[i] += GameConstants.CARD_ESPECIAL_TYPE_GUI;
			}
		}
		cards[hand_card_count] = operate_card + GameConstants.CARD_ESPECIAL_TYPE_HU;
		hand_card_count++;
		this.operate_show_card(seat_index, GameConstants.Show_Card_HU, hand_card_count, cards,
				GameConstants.INVALID_SEAT);

		return;
	}

	/**
	 * 处理仙桃晃晃胡牌显示
	 * 
	 * @param seat_index
	 * @param operate_card
	 * @param rm
	 */
	public void process_chi_hu_player_operate_xthh(int seat_index, int operate_card, boolean rm) {
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		// filtrate_right(seat_index, chr);

		int effect_count = chr.type_count;
		long effect_indexs[] = new long[effect_count];
		for (int i = 0; i < effect_count; i++) {
			if (chr.type_list[i] == GameConstants.CHR_SHU_FAN) {
				effect_indexs[i] = GameConstants.CHR_HU;
			} else {
				effect_indexs[i] = chr.type_list[i];
			}

		}

		// 效果
		this.operate_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_HU, effect_count, effect_indexs, 1,
				GameConstants.INVALID_SEAT);

		// 手牌删掉
		this.operate_player_cards(seat_index, 0, null, 0, null);

		if (rm) {
			// 把摸的牌从手牌删掉,结算的时候不显示这张牌的
			GRR._cards_index[seat_index][_logic.switch_to_card_index(operate_card)]--;
		}

		// 显示胡牌
		int cards[] = new int[GameConstants.MAX_COUNT];
		int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[seat_index], cards);
		for (int i = 0; i < hand_card_count; i++) {
			if (_logic.is_magic_card(cards[i])) {
				cards[i] += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
			}
		}
		cards[hand_card_count] = operate_card + GameConstants.CARD_ESPECIAL_TYPE_HU;
		hand_card_count++;
		this.operate_show_card(seat_index, GameConstants.Show_Card_HU, hand_card_count, cards,
				GameConstants.INVALID_SEAT);

		return;
	}

	public void process_chi_hu_player_score_xthh(int seat_index, int provide_index, int operate_card, boolean zimo) {
		GRR._chi_hu_card[seat_index][0] = operate_card;

		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = _logic.get_chi_hu_action_rank_xthh(chr);// 番数

		// 统计
		if (zimo) {
			// 自摸
			// _player_result.zi_mo_count[seat_index]++;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				// GRR._hu_result[i] = MJGameConstants.HU_RESULT_NULL;
				if (i == seat_index) {
					// GRR._hu_result[i] = MJGameConstants.HU_RESULT_ZIMO;
					continue;
				}

				GRR._lost_fan_shu[i][seat_index] = wFanShu;
			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//
			//
			// GRR._hu_result[provide_index] =
			// MJGameConstants.HU_RESULT_FANGPAO;
			// GRR._hu_result[seat_index] = MJGameConstants.HU_RESULT_JIEPAO;
		}

		float lChiHuScore = wFanShu * this._di_fen;// * GRR._bei_shu;

		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				float s = lChiHuScore;

				s *= this.get_piao_lai_bei_shu(seat_index, i);
				// 胡牌分
				GRR._game_score[i] -= s;
				GRR._game_score[seat_index] += s;

			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			float s = lChiHuScore;
			s *= this.get_piao_lai_bei_shu(seat_index, provide_index);
			// 如果是抢杠胡
			if (!(chr.opr_and(GameConstants.CHR_HUBEI_QIANG_GANG_HU).is_empty())) {
				// s+=(3* this._di_fen * GRR._bei_shu);
			} else if (!(chr.opr_and(GameConstants.CHR_HUBEI_RE_CHONG).is_empty())) {
				int cbGangIndex = GRR._gang_score[provide_index].gang_count;
				s += GRR._gang_score[provide_index].scores[cbGangIndex - 1][provide_index];// 加上杠的分数

			}

			GRR._game_score[provide_index] -= s;
			GRR._game_score[seat_index] += s;

			// 点炮的时候，删掉这张牌显示
			// GRR._cards_index[seat_index][_logic.switch_to_card_index(_provide_card)]--;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		// 设置变量
		GRR._provider[seat_index] = provide_index;
		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();

	}

	/**
	 * 安阳麻将算分
	 * 
	 * @param seat_index
	 * @param provide_index
	 * @param operate_card
	 * @param zimo
	 */
	public void process_chi_hu_player_score_ay(int seat_index, int provide_index, int operate_card, boolean zimo) {
		GRR._chi_hu_card[seat_index][0] = operate_card;

		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = _logic.get_chi_hu_action_rank_ay(chr);// 番数

		// 统计
		if (zimo) {
			// 自摸
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					// GRR._hu_result[i] = MJGameConstants.HU_RESULT_ZIMO;
					continue;
				}

				GRR._lost_fan_shu[i][seat_index] = wFanShu;
			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//
		}

		float lChiHuScore = wFanShu;

		// 花
		lChiHuScore += chr.hua_count;
		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				float s = lChiHuScore;

				if (i == GRR._banker_player || seat_index == GRR._banker_player) {
					s += 5;
					s += (_player_result.qiang[i] + _player_result.qiang[seat_index]);
				} else {
					s += 1;
				}

				s += (_player_result.pao[i] + _player_result.pao[seat_index]);

				// 跑和呛

				s *= 2;

				// 胡牌分
				GRR._game_score[i] -= s;
				GRR._game_score[seat_index] += s;

			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			float s = lChiHuScore;

			if (provide_index == GRR._banker_player || seat_index == GRR._banker_player) {
				s += 5;
				s += (_player_result.qiang[provide_index] + _player_result.qiang[seat_index]);
			} else {
				s += 1;
			}

			s += (_player_result.pao[provide_index] + _player_result.pao[seat_index]);

			// 跑和呛

			GRR._game_score[provide_index] -= s;
			GRR._game_score[seat_index] += s;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		// 设置变量
		GRR._provider[seat_index] = provide_index;
		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();
	}

	/**
	 * 河南麻将算分
	 * 
	 * @param seat_index
	 * @param provide_index
	 * @param operate_card
	 * @param zimo
	 */
	public void process_chi_hu_player_score_henan(int seat_index, int provide_index, int operate_card, boolean zimo) {
		GRR._chi_hu_card[seat_index][0] = operate_card;

		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = _logic.get_chi_hu_action_rank_henan(chr);// 番数

		// 统计
		if (zimo) {
			// 自摸
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					// GRR._hu_result[i] = MJGameConstants.HU_RESULT_ZIMO;
					continue;
				}
				GRR._lost_fan_shu[i][seat_index] = wFanShu;
			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//
		}

		float lChiHuScore = wFanShu;

		// 胡的人是庄家
		boolean zhuang_hu = (GRR._banker_player == seat_index ? true : false);
		// 是否是庄家放胡
		boolean zhuang_fang_hu = (GRR._banker_player == provide_index ? true : false);
		// 是否庄家加底
		boolean jia_di = has_rule(GameConstants.GAME_RULE_HENAN_JIA_DI);

		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				float s = lChiHuScore;

				if (jia_di) {// 庄家加底
					if (zhuang_hu) {
						s += 1;// 庄家hu 别人多输一分
					} else if (GRR._banker_player == i) {// 别人hu 庄家多输一分
						s += 1;
					}
				}
				// 跑
				s += (_player_result.pao[i] + _player_result.pao[seat_index]);

				if (!(chr.opr_and(GameConstants.CHR_HENAN_GANG_KAI)).is_empty()
						&& has_rule(GameConstants.GAME_RULE_HENAN_GANGHUA_DOUBLE)) {
					s *= 2;
				}
				if (!(chr.opr_and(GameConstants.CHR_HENAN_QI_XIAO_DUI)).is_empty()
						&& has_rule(GameConstants.GAME_RULE_HENAN_QIDUI_DOUBLE)) {
					s *= 2;
				}
				if (!(chr.opr_and(GameConstants.CHR_HENAN_QISHOU_HU)).is_empty()) {
					s *= 2;
				}

				// 胡牌分
				GRR._game_score[i] -= s;
				GRR._game_score[seat_index] += s;

			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			float s = lChiHuScore;

			if (jia_di) {// 庄家加底
				if (zhuang_hu) {
					s += 1;// 庄家杆 别人多输一分
				} else if (zhuang_fang_hu) {// 庄家放杆 庄家多输一分
					s += 1;
				}
			}

			s += (_player_result.pao[provide_index] + _player_result.pao[seat_index]);

			if (!(chr.opr_and(GameConstants.CHR_HENAN_GANG_KAI)).is_empty()
					&& has_rule(GameConstants.GAME_RULE_HENAN_GANGHUA_DOUBLE)) {
				s *= 2;
			}
			if (!(chr.opr_and(GameConstants.CHR_HENAN_QI_XIAO_DUI)).is_empty()
					&& has_rule(GameConstants.GAME_RULE_HENAN_QIDUI_DOUBLE)) {
				s *= 2;
			}
			if (!(chr.opr_and(GameConstants.CHR_HENAN_QISHOU_HU)).is_empty()) {
				s *= 2;
			}

			// 跑和呛
			GRR._game_score[provide_index] -= s;
			GRR._game_score[seat_index] += s;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		// 设置变量
		GRR._provider[seat_index] = provide_index;
		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();
	}

	/**
	 * 林州麻将算分
	 * 
	 * @param seat_index
	 * @param provide_index
	 * @param operate_card
	 * @param zimo
	 */
	public void process_chi_hu_player_score_lz(int seat_index, int provide_index, int operate_card, boolean zimo) {
		GRR._chi_hu_card[seat_index][0] = operate_card;

		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = _logic.get_chi_hu_action_rank_lz(chr);// 番数

		// 统计
		if (zimo) {
			// 自摸
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					// GRR._hu_result[i] = MJGameConstants.HU_RESULT_ZIMO;
					continue;
				}

				GRR._lost_fan_shu[i][seat_index] = wFanShu;
			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//
		}

		float lChiHuScore = wFanShu;

		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				float s = lChiHuScore * 2;
				// 跑
				s += (_player_result.pao[i] + _player_result.pao[seat_index]);
				// 胡牌分
				GRR._game_score[i] -= s;
				GRR._game_score[seat_index] += s;

			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			float s = lChiHuScore;

			s += (_player_result.pao[provide_index] + _player_result.pao[seat_index]);

			// 跑和呛

			GRR._game_score[provide_index] -= s;
			GRR._game_score[seat_index] += s;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		// 设置变量
		GRR._provider[seat_index] = provide_index;
		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();
	}

	/**
	 * 胡牌算分
	 * 
	 * @param seat_index
	 * @param provide_index
	 * @param operate_card
	 * @param zimo
	 */
	public void process_chi_hu_player_score(int seat_index, int provide_index, int operate_card, boolean zimo) {

		GRR._chi_hu_card[seat_index][0] = operate_card;

		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = 0;// 番数
		if (_game_type_index == GameConstants.GAME_TYPE_ZZ) {

			wFanShu = _logic.get_chi_hu_action_rank_zz(chr);
		} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			wFanShu = _logic.get_chi_hu_action_rank_hz(chr);
		} else if (_game_type_index == GameConstants.GAME_TYPE_CS) {
			wFanShu = _logic.get_chi_hu_action_rank_cs(chr);
		} else if (_game_type_index == GameConstants.GAME_TYPE_SHUANGGUI) {
			wFanShu = _logic.get_chi_hu_action_rank_sg(chr);
		} else if (_game_type_index == GameConstants.GAME_TYPE_ZHUZHOU) {
			if (has_rule(GameConstants.GAME_RULE_HUNAN_SCORE_MUTIP)) {
				wFanShu = _logic.get_chi_hu_action_rank_zhuzhou_mutip(chr);
			} else {
				wFanShu = _logic.get_chi_hu_action_rank_zhuzhou(chr);
			}
		}

		int lChiHuScore = wFanShu * GameConstants.CELL_SCORE;// wFanShu*m_pGameServiceOption->lCellScore;

		// 杠上炮,呼叫转移 如果开杠者掷骰子补张，补张的牌开杠者若不能胡而其他玩家可以胡属于杠上炮，若胡，则属于杠上开花
		if (!(chr.opr_and(GameConstants.CHR_HUNAN_GANG_SHANG_PAO).is_empty())) {
			/**
			 * int cbGangIndex = GRR._gang_score[_provide_player].gang_count -
			 * 1;// 最后的杠
			 * 
			 * GRR._hu_result[_provide_player] =
			 * MJGameConstants.HU_RESULT_FANGPAO; int cbChiHuCount = 0; for (int
			 * i = 0; i < MJGameConstants.GAME_PLAYER; i++) { if
			 * (GRR._chi_hu_rights[i].is_valid()){//这个胡是有效的 cbChiHuCount++;
			 * GRR._hu_result[i] = MJGameConstants.HU_RESULT_JIEPAO; } }
			 * 
			 * //处理杠的分数 if (cbChiHuCount == 1) { int lScore =
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][seat_index];
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][seat_index]
			 * =
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][_provide_player];
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][_provide_player]
			 * = lScore;
			 * 
			 * } else { // 一炮多响的情况下,胡牌者平分杠得分
			 * 
			 * int lGangScore =
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][_provide_player]/
			 * cbChiHuCount; // 不能小于一番 lGangScore = Math.max(lGangScore,
			 * MJGameConstants.CELL_SCORE); for (int i = 0; i <
			 * MJGameConstants.GAME_PLAYER; i++) { if
			 * (GRR._chi_hu_rights[i].is_valid())
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][i] =
			 * lGangScore; }
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][_provide_player]
			 * = 0;//自己的清空掉 for (int i = 0; i < MJGameConstants.GAME_PLAYER;
			 * i++) { if (i != _provide_player)
			 * GRR._gang_score[_provide_player].scores[cbGangIndex][_provide_player]
			 * += GRR._gang_score[_provide_player].scores[cbGangIndex][i]; }
			 * 
			 * _banker_select = _provide_player; }
			 **/
		}
		// 抢杠杠分不算 玩家在明杠的时候，其他玩家可以胡被杠的此张牌，叫抢杠胡
		else if (!(chr.opr_and(GameConstants.CHR_HUNAN_QIANG_GANG_HU).is_empty())) {
			// GRR._gang_score[_provide_player].gang_count--;//这个杠就不算了
			// _player_result.ming_gang_count[_provide_player]--;

		}

		int real_provide_index = GameConstants.INVALID_SEAT;
		if (is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			// 如果有清一色/碰碰胡
			if (!(chr.opr_and(GameConstants.CHR_HUNAN_QING_YI_SE).is_empty())) {
				// 算坎
				// 第三坎
				if (GRR._weave_count[seat_index] > 2) {
					if (GRR._weave_items[seat_index][2].provide_player != seat_index) {
						real_provide_index = GRR._weave_items[seat_index][2].provide_player;
					}
				}
				// 第四坎
				if (GRR._weave_count[seat_index] > 3) {
					if (GRR._weave_items[seat_index][3].provide_player != seat_index) {
						real_provide_index = GRR._weave_items[seat_index][3].provide_player;
					}
				}
			}
		}

		// 统计
		if (zimo) {
			// 自摸
			// _player_result.zi_mo_count[seat_index]++;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				// GRR._hu_result[i] = MJGameConstants.HU_RESULT_NULL;
				if (i == seat_index) {
					// GRR._hu_result[i] = MJGameConstants.HU_RESULT_ZIMO;
					continue;
				}

				if (real_provide_index == GameConstants.INVALID_SEAT) {
					GRR._lost_fan_shu[i][seat_index] = wFanShu;
				} else {
					// 全包
					GRR._lost_fan_shu[real_provide_index][seat_index] = wFanShu;
				}

			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//

			// GRR._hu_result[provide_index] =
			// MJGameConstants.HU_RESULT_FANGPAO;
			// GRR._hu_result[seat_index] = MJGameConstants.HU_RESULT_JIEPAO;
		}

		/////////////////////////////////////////////// 算分//////////////////////////
		GRR._count_pick_niao = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < GRR._player_niao_count[i]; j++) {
				//////////////////////////////////////////////// 转转麻将抓鸟//////////////////只要159就算
				if ((GameConstants.GAME_TYPE_ZZ == _game_type_index) || is_mj_type(GameConstants.GAME_TYPE_HZ)
						|| is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
					int nValue = GRR._player_niao_cards[i][j];
					nValue = nValue > 1000 ? (nValue - 1000) : nValue;
					int v = _logic.get_card_value(nValue);
					if (v == 1 || v == 5 || v == 9) {
						GRR._count_pick_niao++;
						if (GRR._player_niao_cards[i][j] < 1000) {
							GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
						}
					}
				} else if (GameConstants.GAME_TYPE_CS == _game_type_index) {
					//////////////////////////////////////////////// 长沙麻将抓鸟//////////////////
					if (zimo) {
						GRR._count_pick_niao++;
						if (GRR._player_niao_cards[i][j] < 1000)
							GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
					} else {
						if (seat_index == i || provide_index == i) {// 自己还有放炮的人有效
							GRR._count_pick_niao++;
							if (GRR._player_niao_cards[i][j] < 1000)
								GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
						}
					}
				} else if (GameConstants.GAME_TYPE_ZHUZHOU == _game_type_index) {
					//////////////////////////////////////////////// 长沙麻将抓鸟//////////////////
					if (zimo) {
						GRR._count_pick_niao++;
						if (GRR._player_niao_cards[i][j] < 1000)
							GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
					} else {
						if (seat_index == i || provide_index == i) {// 自己还有放炮的人有效
							GRR._count_pick_niao++;
							if (GRR._player_niao_cards[i][j] < 1000)
								GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
						}
					}
				}

			}
		}

		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				int s = lChiHuScore;
				if (this.is_mj_type(GameConstants.GAME_TYPE_ZZ)) {
					//////////////////////////////////////////////// 转转麻将自摸算分//////////////////
					s += GRR._count_pick_niao;// 只算自己的
				} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
					s += GRR._count_pick_niao * 2;

				} else if (is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
					s += GRR._count_pick_niao * 2;

				} else if (this.is_mj_type(GameConstants.GAME_TYPE_CS)) {
					//////////////////////////////////////////////// 长沙麻将自摸算分//////////////////
					if (this.is_zhuang_xian()) {
						if ((GRR._banker_player == i) || (GRR._banker_player == seat_index)) {
							int zx = lChiHuScore / 6;
							s += (zx == 0 ? 1 : zx);
						}
					}
					GRR._player_niao_invalid[i] = 1;// 庄家鸟生效

					int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[i];
					if (niao > 0) {
						s *= (niao + 1);
					}
				} else if (this.is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
					//////////////////////////////////////////////// 株洲麻将自摸算分//////////////////
					GRR._player_niao_invalid[i] = 1;// 庄家鸟生效

					int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[i];
					if (niao > 0) {
						s += niao;
					}
				}
				// 胡牌分
				if (real_provide_index == GameConstants.INVALID_SEAT) {
					GRR._game_score[i] -= s;
				} else {
					int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[i];
					if (niao > 0) {
						s -= niao;// 鸟要最后处理,把上面加的鸟分减掉 ----先这样处理--年后拆分出来
					}
					if (i == GameConstants.GAME_PLAYER - 1) {// 循环到最后一次 才把鸟分加上
						niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[real_provide_index];
						if (niao > 0) {
							s += niao;
						}
					}

					// 全包
					GRR._game_score[real_provide_index] -= s;
				}
				GRR._game_score[seat_index] += s;
			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			int s = lChiHuScore;
			if (this.is_zhuang_xian()) {
				if ((GRR._banker_player == provide_index) || (GRR._banker_player == seat_index)) {
					int zx = GRR._chi_hu_rights[seat_index].da_hu_count;// lChiHuScore/6;
					s += (zx == 0 ? 1 : zx);
				}
			}
			if (GameConstants.GAME_TYPE_ZZ == _game_type_index) {
				s += GRR._count_pick_niao;// 只算自己的

			} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
				// 如果是抢杠胡
				if (!(chr.opr_and(GameConstants.CHR_HUNAN_QIANG_GANG_HU).is_empty())) {
					// 这个玩家全包
					for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
						if (i == seat_index) {
							continue;
						}
						s += GRR._count_pick_niao * 2;//
					}
				} else {
					s += GRR._count_pick_niao * 2;//
				}

			} else if (GameConstants.GAME_TYPE_CS == _game_type_index) {
				int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[provide_index];
				if (niao > 0) {
					s *= (niao + 1);
				}
			} else if (GameConstants.GAME_TYPE_ZHUZHOU == _game_type_index) {

			}

			if (_game_type_index == GameConstants.GAME_TYPE_ZHUZHOU) {
				if (real_provide_index == GameConstants.INVALID_SEAT) {
					int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[provide_index];
					if (niao > 0) {
						s += niao;
					}
					GRR._game_score[provide_index] -= s;
				} else {
					s *= 3;
					int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[real_provide_index];
					if (niao > 0) {
						s += niao;
					}
					GRR._game_score[real_provide_index] -= s;
				}
			} else {
				if (real_provide_index == GameConstants.INVALID_SEAT) {
					GRR._game_score[provide_index] -= s;
				} else {
					s *= 3;
					GRR._game_score[provide_index] -= s;
				}
			}

			GRR._game_score[seat_index] += s;

			// 点炮的时候，删掉这张牌显示
			// GRR._cards_index[seat_index][_logic.switch_to_card_index(_provide_card)]--;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		if (real_provide_index == GameConstants.INVALID_SEAT) {
			GRR._provider[seat_index] = provide_index;
		} else {
			GRR._provider[seat_index] = real_provide_index;
			GRR._hu_result[real_provide_index] = GameConstants.HU_RESULT_FANG_KAN_QUAN_BAO;
		}
		// 设置变量

		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();

		return;
	}

	public void process_chi_hu_player_score_hnhz(int seat_index, int provide_index, int operate_card, boolean zimo) {
		GRR._chi_hu_card[seat_index][0] = operate_card;

		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = 0;// 番数
		wFanShu = _logic.get_chi_hu_action_rank_hnhz(chr);

		int lChiHuScore = wFanShu * GameConstants.CELL_SCORE;// wFanShu*m_pGameServiceOption->lCellScore;

		// 统计
		if (zimo) {
			// 自摸
			// _player_result.zi_mo_count[seat_index]++;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				// GRR._hu_result[i] = MJGameConstants.HU_RESULT_NULL;
				if (i == seat_index) {
					// GRR._hu_result[i] = MJGameConstants.HU_RESULT_ZIMO;
					continue;
				}

				GRR._lost_fan_shu[i][seat_index] = wFanShu;

			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//
		}

		/////////////////////////////////////////////// 算分//////////////////////////
		GRR._count_pick_niao = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < GRR._player_niao_count[i]; j++) {
				//////////////////////////////////////////////// 转转麻将抓鸟//////////////////只要159就算
				int nValue = GRR._player_niao_cards[i][j];
				nValue = nValue > 1000 ? (nValue - 1000) : nValue;
				int v = _logic.get_card_value(nValue);
				if (v == 1 || v == 5 || v == 9) {
					GRR._count_pick_niao++;
					if (GRR._player_niao_cards[i][j] < 1000) {
						GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
					}
				}

			}
		}

		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				int s = lChiHuScore;
				s += GRR._count_pick_niao * 2;
				GRR._game_score[i] -= s;
				GRR._game_score[seat_index] += s;
			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			int s = lChiHuScore;
			// if (this.is_zhuang_xian()) {
			// if ((GRR._banker_player == provide_index) || (GRR._banker_player
			// == seat_index)) {
			// int zx = GRR._chi_hu_rights[seat_index].da_hu_count;//
			// lChiHuScore/6;
			// s += (zx == 0 ? 1 : zx);
			// }
			// }
			// 如果是抢杠胡
			if (!(chr.opr_and(GameConstants.CHR_HENAN_QIANG_GANG_HU).is_empty())) {
				// 这个玩家全包
				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					if (i == seat_index) {
						continue;
					}
					s += GRR._count_pick_niao * 2;//
				}
			} else {
				s += GRR._count_pick_niao * 2;//
			}

			GRR._game_score[provide_index] -= s;

			GRR._game_score[seat_index] += s;

			// 点炮的时候，删掉这张牌显示
			// GRR._cards_index[seat_index][_logic.switch_to_card_index(_provide_card)]--;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		GRR._provider[seat_index] = provide_index;
		// 设置变量

		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();

		return;
	}

	/**
	 * 长沙胡牌算分
	 * 
	 * @param seat_index
	 * @param provide_index
	 * @param operate_card
	 * @param zimo
	 */
	public void process_chi_hu_player_score_cs(int seat_index, int provide_index, boolean zimo) {
		GRR._win_order[seat_index] = 1;
		// 引用权位
		ChiHuRight chr = GRR._chi_hu_rights[seat_index];

		int wFanShu = 0;// 番数
		wFanShu = _logic.get_chi_hu_action_rank_cs(chr);
		int lChiHuScore = wFanShu * GameConstants.CELL_SCORE;// wFanShu*m_pGameServiceOption->lCellScore;

		// 统计
		if (zimo) {
			// 自摸
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}
				GRR._lost_fan_shu[i][seat_index] = wFanShu;
			}
		} else {// 点炮
			GRR._lost_fan_shu[provide_index][seat_index] = wFanShu;//

		}

		/////////////////////////////////////////////// 算分//////////////////////////
		GRR._count_pick_niao = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			for (int j = 0; j < GRR._player_niao_count[i]; j++) {
				//////////////////////////////////////////////// 长沙麻将抓鸟//////////////////
				if (zimo) {
					GRR._count_pick_niao++;
					if (GRR._player_niao_cards[i][j] < 1000)
						GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
				} else {
					if (seat_index == i || provide_index == i) {// 自己还有放炮的人有效
						GRR._count_pick_niao++;
						if (GRR._player_niao_cards[i][j] < 1000)
							GRR._player_niao_cards[i][j] += 1000;// 胡牌的鸟生效
					}
				}

			}
		}

		////////////////////////////////////////////////////// 自摸 算分
		if (zimo) {
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (i == seat_index) {
					continue;
				}

				int s = lChiHuScore;
				//////////////////////////////////////////////// 长沙麻将自摸算分//////////////////
				if (this.is_zhuang_xian()) {
					if ((GRR._banker_player == i) || (GRR._banker_player == seat_index)) {
						int zx = lChiHuScore / 6;
						s += (zx == 0 ? 1 : zx);
					}
				}
				GRR._player_niao_invalid[i] = 1;// 庄家鸟生效

				int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[i];
				if (niao > 0) {
					s *= (niao + 1);
				}
				// 胡牌分
				GRR._game_score[i] -= s;
				GRR._game_score[seat_index] += s;
			}
		}
		////////////////////////////////////////////////////// 点炮 算分
		else {
			int s = lChiHuScore;
			if (this.is_zhuang_xian()) {
				if ((GRR._banker_player == provide_index) || (GRR._banker_player == seat_index)) {
					int zx = GRR._chi_hu_rights[seat_index].da_hu_count;// lChiHuScore/6;
					s += (zx == 0 ? 1 : zx);
				}
			}
			int niao = GRR._player_niao_count[seat_index] + GRR._player_niao_count[provide_index];
			if (niao > 0) {
				s *= (niao + 1);
			}

			GRR._game_score[provide_index] -= s;
			GRR._game_score[seat_index] += s;

			// 点炮的时候，删掉这张牌显示
			// GRR._cards_index[seat_index][_logic.switch_to_card_index(_provide_card)]--;

			GRR._chi_hu_rights[provide_index].opr_or(GameConstants.CHR_FANG_PAO);

		}

		GRR._provider[seat_index] = provide_index;
		// 设置变量

		_status_gang = false;
		_status_gang_hou_pao = false;

		_playerStatus[seat_index].clean_status();

		return;
	}

	// 发送场景
	// public boolean send_game_data(int seat_index, int cbGameStatus, boolean
	// bSendSecret) {
	// if (cbGameStatus == 0) {
	// cbGameStatus = _game_status;
	// }
	// switch (cbGameStatus) {
	// case MJGameConstants.GS_MJ_FREE: // 空闲状态
	// {
	//
	// // 构造数据
	//
	// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
	// roomResponse.setType(10);
	// this.load_common_status(roomResponse);
	//
	// //
	// this.load_room_info_data(roomResponse);
	// this.load_player_info_data(roomResponse);
	//
	// this.send_response_to_player(seat_index, roomResponse);
	//
	// // 发送场景
	// // return
	// //
	// m_pITableFrame->SendGameScene(pIServerUserItem,&StatusFree,sizeof(StatusFree));
	// break;
	// }
	// case MJGameConstants.GS_MJ_PLAY: // 游戏状态
	// {
	// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
	//
	// this.load_common_status(roomResponse);
	// roomResponse.setType(1);
	//
	// TableResponse.Builder tableResponse = TableResponse.newBuilder();
	//
	// // 玩家
	// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	// RoomPlayerResponse.Builder roomPlayerResponse =
	// RoomPlayerResponse.newBuilder();
	// roomPlayerResponse.setAccountId(this.get_players()[i].getAccount_id());
	// roomPlayerResponse.setUserName("ddshgjjrtiytity");
	// roomPlayerResponse.setHeadImgUrl("");
	// roomPlayerResponse.setIp("");
	// roomPlayerResponse.setSeatIndex(this.get_players()[i].get_seat_index());
	//
	// roomResponse.addPlayers(roomPlayerResponse);
	// }
	//
	// // 游戏变量
	// tableResponse.setBankerPlayer(GRR._banker_player);
	// tableResponse.setCurrentPlayer(_current_player);
	// tableResponse.setCellScore(0);
	//
	// // 状态变量
	// tableResponse.setActionCard(_provide_card);
	// tableResponse.setLeftCardCount(GRR._left_card_count);
	// //tableResponse.setActionMask((_response[seat_index] == false) ?
	// _player_action[seat_index] : MJGameConstants.WIK_NULL);
	//
	// // 历史记录
	// tableResponse.setOutCardData(_out_card_data);
	// tableResponse.setOutCardPlayer(_out_card_player);
	//
	// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
	// tableResponse.addTrustee(false);// 是否托管
	// // 剩余牌数
	// tableResponse.addDiscardCount(GRR._discard_count[i]);
	// Int32ArrayResponse.Builder int_array = Int32ArrayResponse.newBuilder();
	// for (int j = 0; j < 55; j++) {
	// int_array.addItem(GRR._discard_cards[i][j]);
	// }
	// tableResponse.addDiscardCards(int_array);
	//
	// // 组合扑克
	// tableResponse.addWeaveCount(GRR._weave_count[i]);
	// WeaveItemResponseArrayResponse.Builder weaveItem_array =
	// WeaveItemResponseArrayResponse.newBuilder();
	// for (int j = 0; j < MJGameConstants.MAX_WEAVE; j++) {
	// WeaveItemResponse.Builder weaveItem_item =
	// WeaveItemResponse.newBuilder();
	// weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
	// weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
	// weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
	// weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
	// weaveItem_array.addWeaveItem(weaveItem_item);
	// }
	// tableResponse.addWeaveItemArray(weaveItem_array);
	//
	// //
	// tableResponse.addWinnerOrder(0);
	//
	// // 牌
	// tableResponse.addCardCount(_logic.get_card_count_by_index(GRR._cards_index[i]));
	//
	// }
	//
	// // //扑克数据
	// tableResponse.setSendCardData(((_send_card_data != 0) && (_provide_player
	// == seat_index)) ? _send_card_data: MJGameConstants.INVALID_VALUE);
	//
	//
	//
	// int hand_cards[] = new int[MJGameConstants.MAX_COUNT];
	//
	// int hand_card_count =
	// _logic.switch_to_cards_data(GRR._cards_index[seat_index], hand_cards);
	// if(seat_index == _current_player){
	// _logic.remove_card_by_data(hand_cards, _send_card_data);
	// }
	// // tableResponse.setCardCount(hand_card_count);
	// for (int j = 0; j < MJGameConstants.MAX_COUNT; j++) {
	// tableResponse.addCardsData(hand_cards[j]);
	// }
	//
	// // //变量定义
	// // CMD_StatusPlay StatusPlay = new CMD_StatusPlay();
	// // StatusPlay.card_count =
	// // _logic.switch_to_cards_data(_cards_index[seat_index],
	// // StatusPlay.cards_data);
	// //
	// // //游戏变量
	// // StatusPlay.banker_player=_banker_player;
	// // StatusPlay.current_player=_current_player;
	// // StatusPlay.cell_score=0;///m_pGameServiceOption->lCellScore;
	// //
	// // //状态变量
	// // StatusPlay.action_card=_provide_card;
	// // StatusPlay.left_card_count=GRR._left_card_count;
	// //
	// StatusPlay.action_mask=(_response[seat_index]==false)?_player_action[seat_index]:MJGameConstants.WIK_NULL;
	// //
	// // //历史记录
	// // StatusPlay.out_card_player=_out_card_player;
	// // StatusPlay.out_card_data=_out_card_data;
	// // for (int i = 0;i<MJGameConstants.GAME_PLAYER;i++)
	// // {
	// // StatusPlay.trustee[i] = false;
	// // //剩余牌数
	// // StatusPlay.discard_count[i] = GRR._discard_count[i];
	// // //组合扑克
	// // StatusPlay.weave_count[i] = _weave_count[i];
	// //
	// // for(int j=0; j < 55; j++){
	// // StatusPlay.discard_cards[i][j] = GRR._discard_cards[i][j];
	// //
	// // }
	// //
	// // for(int j=0; j < MJGameConstants.MAX_WEAVE; j++){
	// // //组合扑克
	// // StatusPlay.weave_items[i][j].center_card =
	// // _weave_items[i][j].center_card;
	// // StatusPlay.weave_items[i][j].provide_player =
	// // _weave_items[i][j].provide_player;
	// // StatusPlay.weave_items[i][j].public_card =
	// // _weave_items[i][j].public_card;
	// // StatusPlay.weave_items[i][j].weave_kind =
	// // _weave_items[i][j].weave_kind;
	// // }
	// //
	// //
	// //
	// // }
	// // //扑克数据
	// // StatusPlay.card_count =
	// // _logic.switch_to_cards_data(_cards_index[seat_index],
	// // StatusPlay.cards_data);
	// // StatusPlay.send_card_data
	// //
	// =((_send_card_data!=0)&&(_provide_player==seat_index))?_send_card_data:0x00;
	//
	// // 发送场景
	// // return
	// //
	// m_pITableFrame->SendGameScene(pIServerUserItem,&StatusPlay,sizeof(StatusPlay));
	// roomResponse.setTable(tableResponse);
	//
	// this.send_response_to_player(0, roomResponse);
	//
	// break;
	// }
	// }
	//
	// return false;
	// }

	// 过滤吃胡权值
	private void filtrate_right(int seat_index, ChiHuRight chr) {
		// 权位增加
		// 抢杠
		if (_current_player == GameConstants.INVALID_SEAT && _status_gang) {
			chr.opr_or(GameConstants.CHR_HUNAN_QIANG_GANG_HU);
		}
		// 海底捞
		// if (GRR._left_card_count == 0) {
		// chr.opr_or(MJGameConstants.CHR_HAI_DI_LAO);
		// }
		// 附加权位
		// 杠上花
		if (_current_player == seat_index && _status_gang) {
			chr.opr_or(GameConstants.CHR_HUNAN_GANG_KAI);
		}
		// 杠上炮
		if (_status_gang_hou_pao && !_status_gang) {
			chr.opr_or(GameConstants.CHR_HUNAN_GANG_SHANG_PAO);
		}
	}

	/**
	 * 
	 * @param seat_index
	 * @param card
	 *            固定的鸟
	 * @param show
	 * @param add_niao
	 *            额外奖鸟
	 */
	public void set_niao_card(int seat_index, int card, boolean show, int add_niao) {
		for (int i = 0; i < GameConstants.MAX_NIAO_CARD; i++) {
			GRR._cards_data_niao[i] = GameConstants.INVALID_VALUE;
		}

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			GRR._player_niao_count[i] = 0;
			for (int j = 0; j < GameConstants.MAX_NIAO_CARD; j++) {
				GRR._player_niao_cards[i][j] = GameConstants.INVALID_VALUE;
			}

		}
		GRR._show_bird_effect = show;
		if (card == GameConstants.INVALID_VALUE) {
			GRR._count_niao = get_niao_card_num(true, add_niao);
		} else {
			GRR._count_niao = get_niao_card_num(false, add_niao);
		}

		if (GRR._count_niao > GameConstants.ZHANIAO_0) {
			if (card == GameConstants.INVALID_VALUE) {
				int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
				// if (is_mj_type(MJGameConstants.GAME_TYPE_HZ)) {
				// _logic.switch_to_cards_index(_repertory_card_zz,
				// _all_card_len-GRR._left_card_count, GRR._count_niao,
				// cbCardIndexTemp);
				// } else {
				// _logic.switch_to_cards_index(_repertory_card_cs,
				// _all_card_len-GRR._left_card_count, GRR._count_niao,
				// cbCardIndexTemp);
				// }
				_logic.switch_to_cards_index(_repertory_card, _all_card_len - GRR._left_card_count, GRR._count_niao,
						cbCardIndexTemp);
				// cbCardIndexTemp[0] = 3;
				// cbCardIndexTemp[1] = 0x25;
				GRR._left_card_count -= GRR._count_niao;
				_logic.switch_to_cards_data(cbCardIndexTemp, GRR._cards_data_niao);
			} else {
				for (int i = 0; i < GRR._count_niao; i++) {
					GRR._cards_data_niao[i] = card;
				}
			}
		}
		// 中鸟个数
		GRR._count_pick_niao = _logic.get_pick_niao_count(GRR._cards_data_niao, GRR._count_niao);

		for (int i = 0; i < GRR._count_niao; i++) {
			int nValue = _logic.get_card_value(GRR._cards_data_niao[i]);
			int seat = 0;
			if ((GameConstants.GAME_TYPE_ZZ == _game_type_index) || is_mj_type(GameConstants.GAME_TYPE_HZ)
					|| is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI) || is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
				seat = (seat_index + (nValue - 1) % 4) % 4;
			} else if (GameConstants.GAME_TYPE_CS == _game_type_index
					|| GameConstants.GAME_TYPE_ZHUZHOU == _game_type_index) {
				seat = (GRR._banker_player + (nValue - 1) % 4) % 4;
			}
			GRR._player_niao_cards[seat][GRR._player_niao_count[seat]] = GRR._cards_data_niao[i];
			GRR._player_niao_count[seat]++;
		}
	}

	/**
	 * 获取鸟的 数量
	 * 
	 * @param check
	 * @param add_niao
	 * @return
	 */
	public int get_niao_card_num(boolean check, int add_niao) {
		int nNum = GameConstants.ZHANIAO_0;

		// 湖南麻将的抓鸟
		if (is_mj(GameConstants.GAME_ID_HUNAN)) {
			if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO1)) {
				nNum = GameConstants.ZHANIAO_1;
			} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO2)) {
				nNum = GameConstants.ZHANIAO_2;
			} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO4)) {
				nNum = GameConstants.ZHANIAO_4;
			} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO6)) {
				nNum = GameConstants.ZHANIAO_6;
			}

		} else if (is_mj(GameConstants.GAME_ID_HENAN)) {
			// 河南麻将的抓鸟
			if (has_rule(GameConstants.GAME_RULE_HENAN_ZHANIAO2)) {
				nNum = GameConstants.ZHANIAO_2;
			} else if (has_rule(GameConstants.GAME_RULE_HENAN_ZHANIAO4)) {
				nNum = GameConstants.ZHANIAO_4;
			} else if (has_rule(GameConstants.GAME_RULE_HENAN_ZHANIAO6)) {
				nNum = GameConstants.ZHANIAO_6;
			}
		}

		nNum += add_niao;

		if (check == false) {
			return nNum;
		}
		if (nNum > GRR._left_card_count) {
			nNum = GRR._left_card_count;
		}
		return nNum;
	}

	@Override
	public Player get_player(long account_id) {
		Player player = null;

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			player = this.get_players()[i];
			if (player != null && player.getAccount_id() == account_id) {
				return player;
			}
		}

		return null;
	}

	@Override
	public boolean handler_audio_chat(Player player, com.google.protobuf.ByteString chat, int l, float audio_len) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_AUDIO_CHAT);
		roomResponse.setOperatePlayer(player.get_seat_index());
		roomResponse.setAudioChat(chat);
		roomResponse.setAudioSize(l);
		roomResponse.setAudioLen(audio_len);

		this.send_response_to_other(player.get_seat_index(), roomResponse);
		// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
		// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		// roomResponse.setType(MsgConstants.RESPONSE_AUDIO_CHAT);
		// roomResponse.setOperatePlayer(player.get_seat_index());
		// roomResponse.setAudioChat(chat);
		// roomResponse.setAudioSize(l);
		// roomResponse.setAudioLen(audio_len);
		// this.send_response_to_player(i,roomResponse);
		// }

		return true;
	}

	@Override
	public boolean handler_emjoy_chat(Player player, int id) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_EMJOY_CHAT);
		roomResponse.setOperatePlayer(player.get_seat_index());
		roomResponse.setEmjoyId(id);
		this.send_response_to_room(roomResponse);

		return true;
	}

	@Override
	public boolean handler_requst_location(Player player, LocationInfor locationInfor) {
//		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
//		roomResponse.setType(MsgConstants.RESPONSE_LOCATION);
//		player.locationInfor = locationInfor;
//		for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
//			Player tarPlayer = this.get_players()[j];
//			if (tarPlayer == null || tarPlayer.locationInfor == null)
//				continue;
//			roomResponse.addLocationInfor(tarPlayer.locationInfor);
//		}
//		this.send_response_to_room(roomResponse);
		return true;
	}

	// 18 玩家申请解散房间( operate_code=1发起解散2=同意解散3=不同意解散)
	/*
	 * 
	 * Release_Room_Type_SEND = 1, //发起解散 Release_Room_Type_AGREE, //同意
	 * Release_Room_Type_DONT_AGREE, //不同意 Release_Room_Type_CANCEL,
	 * //还没开始,房主解散房间 Release_Room_Type_QUIT //还没开始,普通玩家退出房间
	 */
	@Override
	public boolean handler_release_room(Player player, int opr_code) {
		int seat_index = player.get_seat_index();

		SysParamModel sysParamModel3007 = SysParamDict.getInstance().getSysParamModelDictionaryByGameId(1).get(3007);
		int delay = 60;
		if (sysParamModel3007 != null) {
			delay = sysParamModel3007.getVal1();
		}

		switch (opr_code) {
		case GameConstants.Release_Room_Type_SEND: {
			//发起解散
			if (GameConstants.GS_MJ_FREE == _game_status ) {
				//游戏还没开始,不能发起解散
				return false;
			}
			// 有人申请解散了
			if (_gameRoomRecord.request_player_seat != GameConstants.INVALID_SEAT) {
				return false;
			}

			// 取消之前的调度
			if (_release_scheduled != null)_release_scheduled.cancel(false);
			_request_release_time = System.currentTimeMillis() + delay * 1000;
			if (GRR == null) {
				_release_scheduled = GameSchedule.put(new GameFinishRunnable(this.getRoom_id(), seat_index,
						GameConstants.Game_End_RELEASE_WAIT_TIME_OUT), delay, TimeUnit.SECONDS);
			} else {
				_release_scheduled = GameSchedule.put(new GameFinishRunnable(this.getRoom_id(), seat_index,
						GameConstants.Game_End_RELEASE_PLAY_TIME_OUT), delay, TimeUnit.SECONDS);
			}

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_gameRoomRecord.release_players[i] = 0;
			}

			_gameRoomRecord.request_player_seat = seat_index;
			_gameRoomRecord.release_players[seat_index] = 1;// 同意

			// 不在线的玩家默认同意
			// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
			// Player pl = this.get_players()[i];
			// if(pl==null || pl.isOnline()==false){
			// _gameRoomRecord.release_players[i] = 1;//同意
			// }
			// }
			int count = 0;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (_gameRoomRecord.release_players[i] == 1) {// 都同意了

					count++;
				}
			}
			if (count == GameConstants.GAME_PLAYER) {
				if (GRR == null) {
					this.handler_game_finish(GameConstants.INVALID_SEAT, GameConstants.Game_End_RELEASE_RESULT);
				} else {
					this.handler_game_finish(GameConstants.INVALID_SEAT, GameConstants.Game_End_RELEASE_PLAY);
				}

				for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
					player = this.get_players()[j];
					if (player == null)
						continue;
					send_error_notify(j, 1, "游戏解散成功!");

				}
				return true;
			}

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			roomResponse.setType(MsgConstants.RESPONSE_PLAYER_RELEASE);
			roomResponse.setReleaseTime(delay);
			roomResponse.setOperateCode(0);
			roomResponse.setRequestPlayerSeat(_gameRoomRecord.request_player_seat);
			roomResponse.setLeftTime(delay);
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				roomResponse.addReleasePlayers(_gameRoomRecord.release_players[i]);
			}
			this.send_response_to_room(roomResponse);

			return false;
		}

		case GameConstants.Release_Room_Type_AGREE: {
			if (GameConstants.GS_MJ_FREE == _game_status ) {
				//游戏还没开始,不能同意解散
				return false;
			}
			// 没人发起解散
			if (_gameRoomRecord.request_player_seat == GameConstants.INVALID_SEAT) {
				return false;
			}
			if (_gameRoomRecord.release_players[seat_index] == 1)
				return false;

			_gameRoomRecord.release_players[seat_index] = 1;

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			roomResponse.setType(MsgConstants.RESPONSE_PLAYER_RELEASE);
			roomResponse.setReleaseTime(delay);
			roomResponse.setRequestPlayerSeat(_gameRoomRecord.request_player_seat);
			roomResponse.setOperateCode(1);
			roomResponse.setLeftTime((_request_release_time - System.currentTimeMillis()) / 1000);
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				roomResponse.addReleasePlayers(_gameRoomRecord.release_players[i]);
			}

			this.send_response_to_room(roomResponse);

			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				if (_gameRoomRecord.release_players[i] != 1) {
					return false;// 有一个不同意
				}
			}

			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散成功!");

			}

			if (_release_scheduled != null)_release_scheduled.cancel(false);
			_release_scheduled = null;
			if (GRR == null) {
				this.handler_game_finish(GameConstants.INVALID_SEAT, GameConstants.Game_End_RELEASE_RESULT);
			} else {
				this.handler_game_finish(GameConstants.INVALID_SEAT, GameConstants.Game_End_RELEASE_PLAY);
			}

			return false;
		}

		case GameConstants.Release_Room_Type_DONT_AGREE: {
			if (GameConstants.GS_MJ_FREE == _game_status ) {
				//游戏还没开始,不能同意解散
				return false;
			}
			if (_gameRoomRecord.request_player_seat == GameConstants.INVALID_SEAT) {
				return false;
			}
			_gameRoomRecord.release_players[seat_index] = 2;
			_gameRoomRecord.request_player_seat = GameConstants.INVALID_SEAT;

			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			roomResponse.setReleaseTime(delay);
			roomResponse.setType(MsgConstants.RESPONSE_PLAYER_RELEASE);
			roomResponse.setRequestPlayerSeat(_gameRoomRecord.request_player_seat);
			roomResponse.setOperateCode(1);
			int l = (int) (_request_release_time - System.currentTimeMillis() / 1000);
			if (l <= 0) {
				l = 1;
			}
			roomResponse.setLeftTime(l);
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				roomResponse.addReleasePlayers(_gameRoomRecord.release_players[i]);
			}
			this.send_response_to_room(roomResponse);

			_request_release_time = 0;
			_gameRoomRecord.request_player_seat = GameConstants.INVALID_SEAT;
			if (_release_scheduled != null)_release_scheduled.cancel(false);
			_release_scheduled = null;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				_gameRoomRecord.release_players[i] = 0;
			}

			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				player = this.get_players()[j];
				if (player == null)
					continue;
				send_error_notify(j, 1, "游戏解散失败!玩家[" + this.get_players()[seat_index].getNick_name() + "]不同意解散");

			}

			return false;
		}

		case GameConstants.Release_Room_Type_CANCEL: {// 房主未开始游戏 解散
			if (GameConstants.GS_MJ_FREE != _game_status ) {
				//游戏已经来时
				return false;
			}
			if (_cur_round == 0) {
				// _gameRoomRecord.request_player_seat =
				// MJGameConstants.INVALID_SEAT;

				RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
				roomResponse.setType(MsgConstants.RESPONSE_FORCE_EXIT);// 直接拉出游戏
				this.send_response_to_room(roomResponse);

				for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
					Player p = this.get_players()[i];
					if (p == null)
						continue;
					if (i == seat_index) {
						send_error_notify(i, 2, "游戏已解散");
					} else {
						send_error_notify(i, 2, "游戏已被创建者解散");
					}

				}
				// 删除房间
				PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());
			} else {
				return false;

			}
		}
			break;
		case GameConstants.Release_Room_Type_QUIT: {// 玩家未开始游戏 退出
			if (GameConstants.GS_MJ_FREE != _game_status ) {
				//游戏已经开始
				return false;
			}
			RoomResponse.Builder quit_roomResponse = RoomResponse.newBuilder();
			quit_roomResponse.setType(MsgConstants.RESPONSE_FORCE_EXIT);// 直接拉出游戏
			send_response_to_player(seat_index, quit_roomResponse);

			send_error_notify(seat_index, 2, "您已退出该游戏");
			this.get_players()[seat_index] = null;
			_player_ready[seat_index] = 0;

			PlayerServiceImpl.getInstance().quitRoomId(player.getAccount_id());

			// 刷新玩家
			RoomResponse.Builder refreshroomResponse = RoomResponse.newBuilder();
			refreshroomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);
			this.load_player_info_data(refreshroomResponse);
			//
			send_response_to_other(seat_index, refreshroomResponse);
		}
		break;

		}

		return true;

	}

	@Override
	public boolean handler_requst_pao_qiang(Player player, int pao, int qiang) {
		// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		// roomResponse.setType(MsgConstants.RESPONSE_EMJOY_CHAT);
		// roomResponse.setOperatePlayer(player.get_seat_index());
		// roomResponse.setEmjoyId(id);
		// this.send_response_to_room(roomResponse);
		if (_handler_pao_qiang != null) {
			return _handler_pao_qiang.handler_pao_qiang(this, player.get_seat_index(), pao, qiang);
		} else if (_handler_pao != null) {
			return _handler_pao.handler_pao_qiang(this, player.get_seat_index(), pao, qiang);
		} else if (_handler_pao_henna != null) {
			return _handler_pao_henna.handler_pao_qiang(this, player.get_seat_index(), pao, qiang);
		}

		return false;

	}

	/////////////////////////////////////////////////////// send///////////////////////////////////////////
	/////
	/**
	 * 基础状态
	 * 
	 * @return
	 */
	public boolean operate_player_status() {
		//// 发送玩家出牌 201 =玩家出牌(CMD_OutCard: operate_player,operate_card)
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_PLAYER_STATUS);
		this.load_common_status(roomResponse);
		return this.send_response_to_room(roomResponse);
	}

	/**
	 * 刷新玩家信息
	 * 
	 * @return
	 */
	public boolean operate_player_data() {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);
		this.load_player_info_data(roomResponse);

		GRR.add_room_response(roomResponse);

		return this.send_response_to_room(roomResponse);
	}

	/**
	 * 在玩家的前面显示出的牌 --- 发送玩家出牌
	 * 
	 * @param seat_index
	 * @param count
	 * @param cards
	 * @param type
	 * @param to_player
	 * @return
	 */
	//
	public boolean operate_out_card(int seat_index, int count, int cards[], int type, int to_player) {
		if (seat_index == GameConstants.INVALID_SEAT) {
			return false;
		}
		//// 发送玩家出牌 201 =玩家出牌(CMD_OutCard: operate_player,operate_card)
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		this.load_common_status(roomResponse);
		roomResponse.setType(MsgConstants.RESPONSE_OUT_CARD);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(type);// 出牌
		roomResponse.setCardCount(count);
		if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY) || is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)
				|| is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			roomResponse.setFlashTime(0);
			roomResponse.setStandTime(250);
		} else {
			roomResponse.setFlashTime(150);
			roomResponse.setStandTime(150);

		}

		for (int i = 0; i < count; i++) {

			roomResponse.addCardData(cards[i]);
		}

		if (to_player == GameConstants.INVALID_SEAT) {
			GRR.add_room_response(roomResponse);
			return this.send_response_to_room(roomResponse);
		} else {
			return this.send_response_to_player(to_player, roomResponse);
		}

	}

	public boolean operate_out_card_bao_ting(int seat_index, int count, int cards[], int type, int to_player) {
		if (seat_index == GameConstants.INVALID_SEAT) {
			return false;
		}
		//// 发送玩家出牌 201 =玩家出牌(CMD_OutCard: operate_player,operate_card)
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		this.load_common_status(roomResponse);
		roomResponse.setType(MsgConstants.RESPONSE_OUT_CARD);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(type);// 出牌
		roomResponse.setCardCount(count);
		if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY) || is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)
				|| is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			roomResponse.setFlashTime(0);
			roomResponse.setStandTime(250);
		} else {
			roomResponse.setFlashTime(150);
			roomResponse.setStandTime(150);
		}

		if (to_player == GameConstants.INVALID_SEAT) {
			for (int i = 0; i < count; i++) {

				roomResponse.addCardData(cards[i]);
			}
			this.send_response_to_player(seat_index, roomResponse);// 自己有值

			GRR.add_room_response(roomResponse);

			roomResponse.clearCardData();
			for (int i = 0; i < count; i++) {

				roomResponse.addCardData(GameConstants.BLACK_CARD);
			}
			this.send_response_to_other(seat_index, roomResponse);// 别人是背着的
		} else {
			if (to_player == seat_index) {
				for (int i = 0; i < count; i++) {
					roomResponse.addCardData(cards[i]);
				}
			} else {
				for (int i = 0; i < count; i++) {
					roomResponse.addCardData(GameConstants.BLACK_CARD);
				}
			}

			this.send_response_to_player(to_player, roomResponse);
		}

		return true;
	}

	// 显示在玩家前面的牌,小胡牌,摸杠牌
	public boolean operate_show_card(int seat_index, int type, int count, int cards[], int to_player) {
		//// 发送玩家出牌 201 =玩家出牌(CMD_OutCard: operate_player,operate_card)
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		this.load_common_status(roomResponse);
		roomResponse.setType(MsgConstants.RESPONSE_SHOW_CARD);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(type);// 出牌
		roomResponse.setCardCount(count);
		for (int i = 0; i < count; i++) {

			roomResponse.addCardData(cards[i]);
		}
		GRR.add_room_response(roomResponse);
		if (to_player == GameConstants.INVALID_SEAT) {
			return this.send_response_to_room(roomResponse);
		} else {
			return this.send_response_to_player(to_player, roomResponse);
		}
	}

	// 添加牌到牌堆
	private boolean operate_add_discard(int seat_index, int count, int cards[]) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		this.load_common_status(roomResponse);
		roomResponse.setType(MsgConstants.RESPONSE_ADD_DISCARD);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(2);// 出牌
		roomResponse.setCardCount(count);
		for (int i = 0; i < count; i++) {
			roomResponse.addCardData(cards[i]);
		}
		GRR.add_room_response(roomResponse);
		this.send_response_to_room(roomResponse);

		return true;
	}

	/**
	 * 效果 (通知玩家弹出 吃碰杆 胡牌==效果)
	 * 
	 * @param seat_index
	 * @param effect_type
	 * @param effect_count
	 * @param effect_indexs
	 * @param time
	 * @param to_player
	 * @return
	 */
	public boolean operate_effect_action(int seat_index, int effect_type, int effect_count, long effect_indexs[],
			int time, int to_player) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_EFFECT_ACTION);
		roomResponse.setEffectType(effect_type);
		roomResponse.setTarget(seat_index);
		roomResponse.setEffectCount(effect_count);
		for (int i = 0; i < effect_count; i++) {
			roomResponse.addEffectsIndex(effect_indexs[i]);
		}

		roomResponse.setEffectTime(time);

		if (to_player == GameConstants.INVALID_SEAT) {
			GRR.add_room_response(roomResponse);
			this.send_response_to_room(roomResponse);
		} else {
			this.send_response_to_player(to_player, roomResponse);
		}

		return true;
	}

	/**
	 * 玩家动作--通知玩家弹出/关闭操作
	 * 
	 * @param seat_index
	 * @param close
	 * @return
	 */
	public boolean operate_player_action(int seat_index, boolean close) {
		PlayerStatus curPlayerStatus = _playerStatus[seat_index];

		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_PLAYER_ACTION);
		roomResponse.setTarget(seat_index);
		this.load_common_status(roomResponse);

		if (close == true) {
			// GRR.add_room_response(roomResponse);
			// 通知玩家关闭
			this.send_response_to_player(seat_index, roomResponse);
			return true;
		}
		for (int i = 0; i < curPlayerStatus._action_count; i++) {
			roomResponse.addActions(curPlayerStatus._action[i]);
		}
		// 组合数据
		for (int i = 0; i < curPlayerStatus._weave_count; i++) {
			WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
			weaveItem_item.setCenterCard(curPlayerStatus._action_weaves[i].center_card);
			weaveItem_item.setProvidePlayer(curPlayerStatus._action_weaves[i].provide_player);
			weaveItem_item.setPublicCard(curPlayerStatus._action_weaves[i].public_card);
			weaveItem_item.setWeaveKind(curPlayerStatus._action_weaves[i].weave_kind);
			roomResponse.addWeaveItems(weaveItem_item);
		}
		// GRR.add_room_response(roomResponse);
		this.send_response_to_player(seat_index, roomResponse);
		return true;
	}

	/**
	 * 发牌
	 * 
	 * @param seat_index
	 * @param count
	 * @param cards
	 * @param to_player
	 * @return
	 */
	public boolean operate_player_get_card(int seat_index, int count, int cards[], int to_player) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		this.load_common_status(roomResponse);
		roomResponse.setType(MsgConstants.RESPONSE_SEND_CARD);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(2);// get牌
		roomResponse.setCardCount(count);

		if (to_player == GameConstants.INVALID_SEAT) {
			this.send_response_to_other(seat_index, roomResponse);

			for (int i = 0; i < count; i++) {
				roomResponse.addCardData(cards[i]);
			}
			GRR.add_room_response(roomResponse);
			return this.send_response_to_player(seat_index, roomResponse);

		} else {
			if (seat_index == to_player) {
				for (int i = 0; i < count; i++) {
					roomResponse.addCardData(cards[i]);
				}
			}
			// GRR.add_room_response(roomResponse);
			return this.send_response_to_player(to_player, roomResponse);
		}

	}

	/**
	 * 刷新玩家的牌--手牌 和 牌堆上的牌
	 * 
	 * @param seat_index
	 * @param card_count
	 * @param cards
	 *            实际的牌数据(手上的牌)
	 * @param weave_count
	 * @param weaveitems
	 *            牌堆中的组合(落地的牌)
	 * @return
	 */
	public boolean operate_player_cards(int seat_index, int card_count, int cards[], int weave_count,
			WeaveItem weaveitems[]) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYER_CARDS);// 12更新玩家手牌数据
		roomResponse.setGameStatus(_game_status);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(1);

		this.load_common_status(roomResponse);

		// 手牌数量
		roomResponse.setCardCount(card_count);
		roomResponse.setWeaveCount(weave_count);
		// 组合牌
		if (weave_count > 0) {
			for (int j = 0; j < weave_count; j++) {
				WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
				weaveItem_item.setProvidePlayer(weaveitems[j].provide_player);
				weaveItem_item.setPublicCard(weaveitems[j].public_card);
				weaveItem_item.setWeaveKind(weaveitems[j].weave_kind);
				weaveItem_item.setCenterCard(weaveitems[j].center_card);
				roomResponse.addWeaveItems(weaveItem_item);
			}
		}

		this.send_response_to_other(seat_index, roomResponse);

		// 手牌--将自己的手牌数据发给自己
		for (int j = 0; j < card_count; j++) {
			roomResponse.addCardData(cards[j]);
		}
		GRR.add_room_response(roomResponse);
		// 自己才有牌数据
		this.send_response_to_player(seat_index, roomResponse);

		return true;
	}

	public boolean operate_player_cards_flag(int seat_index, int card_count, int cards[], int weave_count,
			WeaveItem weaveitems[]) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYER_CARDS);// 12更新玩家手牌数据
		roomResponse.setGameStatus(_game_status);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(2);

		// this.load_common_status(roomResponse);
		//
		// //手牌数量
		// roomResponse.setCardCount(card_count);
		// roomResponse.setWeaveCount(weave_count);
		// //组合牌
		// if (weave_count>0) {
		// for (int j = 0; j < weave_count; j++) {
		// WeaveItemResponse.Builder weaveItem_item =
		// WeaveItemResponse.newBuilder();
		// weaveItem_item.setProvidePlayer(weaveitems[j].provide_player);
		// weaveItem_item.setPublicCard(weaveitems[j].public_card);
		// weaveItem_item.setWeaveKind(weaveitems[j].weave_kind);
		// weaveItem_item.setCenterCard(weaveitems[j].center_card);
		// roomResponse.addWeaveItems(weaveItem_item);
		// }
		// }
		//
		// this.send_response_to_other(seat_index, roomResponse);
		//
		// // 手牌
		// for (int j = 0; j < card_count; j++) {
		// roomResponse.addCardData(cards[j]);
		// }
		GRR.add_room_response(roomResponse);
		// 自己才有牌数据
		this.send_response_to_player(seat_index, roomResponse);

		return true;
	}

	/**
	 * 刷新玩家的牌
	 * 
	 * @param seat_index
	 * @param card_count
	 * @param cards
	 * @param weave_count
	 * @param weaveitems
	 * @return
	 */
	public boolean operate_player_cards_with_ting(int seat_index, int card_count, int cards[], int weave_count,
			WeaveItem weaveitems[]) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYER_CARDS);// 12更新玩家手牌数据
		roomResponse.setGameStatus(_game_status);
		roomResponse.setTarget(seat_index);
		roomResponse.setCardType(1);

		this.load_common_status(roomResponse);

		// 手牌数量
		roomResponse.setCardCount(card_count);
		roomResponse.setWeaveCount(weave_count);
		// 组合牌
		if (weave_count > 0) {
			for (int j = 0; j < weave_count; j++) {
				WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
				weaveItem_item.setProvidePlayer(weaveitems[j].provide_player);
				weaveItem_item.setPublicCard(weaveitems[j].public_card);
				weaveItem_item.setWeaveKind(weaveitems[j].weave_kind);
				weaveItem_item.setCenterCard(weaveitems[j].center_card);
				roomResponse.addWeaveItems(weaveItem_item);
			}
		}

		this.send_response_to_other(seat_index, roomResponse);

		// 手牌
		for (int j = 0; j < card_count; j++) {
			roomResponse.addCardData(cards[j]);
		}

		int ting_count = _playerStatus[seat_index]._hu_out_card_count;

		// 打出去可以听牌的个数
		roomResponse.setOutCardCount(ting_count);

		for (int i = 0; i < ting_count; i++) {
			int ting_card_cout = _playerStatus[seat_index]._hu_out_card_ting_count[i];
			roomResponse.addOutCardTingCount(ting_card_cout);
			if (_logic.is_magic_card(_playerStatus[seat_index]._hu_out_card_ting[i])) {
				roomResponse.addOutCardTing(
						_playerStatus[seat_index]._hu_out_card_ting[i] + GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI);
			} else {
				roomResponse.addOutCardTing(
						_playerStatus[seat_index]._hu_out_card_ting[i] + GameConstants.CARD_ESPECIAL_TYPE_TING);
			}

			Int32ArrayResponse.Builder int_array = Int32ArrayResponse.newBuilder();
			for (int j = 0; j < ting_card_cout; j++) {
				int_array.addItem(_playerStatus[seat_index]._hu_out_cards[i][j]);
			}
			roomResponse.addOutCardTingCards(int_array);
		}

		// 自己才有牌数据
		this.send_response_to_player(seat_index, roomResponse);

		GRR.add_room_response(roomResponse);

		return true;
	}

	/**
	 * 删除牌堆的牌 (吃碰杆的那张牌 从废弃牌堆上移除)
	 * 
	 * @param seat_index
	 * @param discard_index
	 * @return
	 */
	public boolean operate_remove_discard(int seat_index, int discard_index) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_REMOVE_DISCARD);
		roomResponse.setTarget(seat_index);
		roomResponse.setDiscardIndex(discard_index);

		GRR.add_room_response(roomResponse);
		this.send_response_to_room(roomResponse);

		return true;
	}

	/**
	 * 显示胡的牌
	 * 
	 * @param seat_index
	 * @param count
	 * @param cards
	 * @return
	 */
	public boolean operate_chi_hu_cards(int seat_index, int count, int cards[]) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_CHI_HU_CARDS);
		roomResponse.setTarget(seat_index);

		for (int i = 0; i < count; i++) {
			roomResponse.addChiHuCards(cards[i]);
		}

		this.send_response_to_player(seat_index, roomResponse);
		return true;
	}

	/***
	 * 刷新特殊描述
	 * 
	 * @param txt
	 * @param type
	 * @return
	 */
	public boolean operate_especial_txt(String txt, int type) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_ESPECIAL_TXT);
		roomResponse.setEspecialTxtType(type);
		roomResponse.setEspecialTxt(txt);
		this.send_response_to_room(roomResponse);
		return true;
	}

	/**
	 * 牌局中分数结算
	 * 
	 * @param seat_index
	 * @param score
	 * @return
	 */
	public boolean operate_player_score(int seat_index, float[] score) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_GAME_PLAYER_SCORE);
		roomResponse.setTarget(seat_index);
		this.load_common_status(roomResponse);
		this.load_player_info_data(roomResponse);
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			roomResponse.addScore(_player_result.game_score[i]);
			roomResponse.addOpereateScore(score[i]);
		}
		this.send_response_to_room(roomResponse);
		return true;
	}

	// 12更新玩家手牌数据
	// 13更新玩家牌数据(包含吃碰杠组合)
	// public boolean refresh_hand_cards(int seat_index, boolean send_weave) {
	// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
	// if (send_weave == true) {
	// roomResponse.setType(13);// 13更新玩家牌数据(包含吃碰杠组合)
	// } else {
	// roomResponse.setType(12);// 12更新玩家手牌数据
	// }
	// roomResponse.setGameStatus(_game_status);
	// roomResponse.setOperatePlayer(seat_index);
	//
	// int hand_cards[] = new int[MJGameConstants.MAX_COUNT];
	// int hand_card_count =
	// _logic.switch_to_cards_data(GRR._cards_index[seat_index], hand_cards);
	//
	// //手牌数量
	// roomResponse.setCardCount(hand_card_count);
	//
	// // 手牌
	// for (int j = 0; j < hand_card_count; j++) {
	// roomResponse.addCardData(hand_cards[j]);
	// }
	//
	// //组合牌
	// if (send_weave == true) {
	// // 13更新玩家牌数据(包含吃碰杠组合)
	// for (int j = 0; j < GRR._weave_count[seat_index]; j++) {
	// WeaveItemResponse.Builder weaveItem_item =
	// WeaveItemResponse.newBuilder();
	// weaveItem_item.setProvidePlayer(GRR._weave_items[seat_index][j].provide_player);
	// weaveItem_item.setPublicCard(GRR._weave_items[seat_index][j].public_card);
	// weaveItem_item.setWeaveKind(GRR._weave_items[seat_index][j].weave_kind);
	// weaveItem_item.setCenterCard(GRR._weave_items[seat_index][j].center_card);
	// roomResponse.addWeaveItems(weaveItem_item);
	// }
	// }
	// // 自己才有牌数据
	// this.send_response_to_room(roomResponse);
	//
	// return true;
	// }

	// 14摊开玩家手上的牌
	// public boolean send_show_hand_card(int seat_index, int cards[]) {
	// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
	// roomResponse.setType(14);
	// roomResponse.setCardCount(cards.length);
	// roomResponse.setOperatePlayer(seat_index);
	// for (int i = 0; i < cards.length; i++) {
	// roomResponse.addCardData(cards[i]);
	// }
	//
	// this.send_response_to_room(roomResponse);
	// return true;
	// }

	// public boolean refresh_discards(int seat_index) {
	// if (seat_index == MJGameConstants.INVALID_SEAT) {
	// return false;
	// }
	// RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
	// roomResponse.setType(MsgConstants.RESPONSE_REFRESH_DISCARD);
	// roomResponse.setOperatePlayer(seat_index);
	//
	// int l = GRR._discard_count[seat_index];
	// for (int i = 0; i < l; i++) {
	// roomResponse.addCardData(GRR._discard_cards[seat_index][i]);
	// }
	//
	// this.send_response_to_room(roomResponse);
	//
	// return true;
	// }

	public boolean handler_player_offline(Player player) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setGameStatus(_game_status);
		roomResponse.setType(MsgConstants.RESPONSE_REFRESH_PLAYERS);// 刷新玩家
		this.load_player_info_data(roomResponse);

		send_response_to_other(player.get_seat_index(), roomResponse);

		// 解散的时候默认同意解散
		// if(_gameRoomRecord!=null &&
		// (_gameRoomRecord.request_player_seat!=MJGameConstants.INVALID_SEAT)){
		// this.handler_release_room(player,
		// MJGameConstants.Release_Room_Type_AGREE);
		// }

		return true;
	}

	public boolean send_play_data(int seat_index) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_RECONNECT_DATA);

		TableResponse.Builder tableResponse = TableResponse.newBuilder();

		load_room_info_data(roomResponse);
		load_player_info_data(roomResponse);

		this.load_common_status(roomResponse);
		// 游戏变量
		tableResponse.setBankerPlayer(GRR._banker_player);
		tableResponse.setCurrentPlayer(_current_player);
		tableResponse.setCellScore(0);

		// 状态变量
		tableResponse.setActionCard(_provide_card);
		// tableResponse.setActionMask((_response[seat_index] == false) ?
		// _player_action[seat_index] : MJGameConstants.WIK_NULL);

		// 历史记录
		tableResponse.setOutCardData(_out_card_data);
		tableResponse.setOutCardPlayer(_out_card_player);

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			tableResponse.addTrustee(false);// 是否托管
			// 剩余牌数
			tableResponse.addDiscardCount(GRR._discard_count[i]);
			Int32ArrayResponse.Builder int_array = Int32ArrayResponse.newBuilder();
			for (int j = 0; j < 55; j++) {
				int_array.addItem(GRR._discard_cards[i][j]);
			}
			tableResponse.addDiscardCards(int_array);

			// 组合扑克
			tableResponse.addWeaveCount(GRR._weave_count[i]);
			WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
			for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
				WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
				weaveItem_item.setCenterCard(GRR._weave_items[i][j].center_card);
				weaveItem_item.setProvidePlayer(GRR._weave_items[i][j].provide_player);
				weaveItem_item.setPublicCard(GRR._weave_items[i][j].public_card);
				weaveItem_item.setWeaveKind(GRR._weave_items[i][j].weave_kind);
				weaveItem_array.addWeaveItem(weaveItem_item);
			}
			tableResponse.addWeaveItemArray(weaveItem_array);

			//
			tableResponse.addWinnerOrder(0);

			if (_status_send == true) {
				// 牌

				if (i == _current_player) {
					tableResponse.addCardCount(_logic.get_card_count_by_index(GRR._cards_index[i]) - 1);
				} else {
					tableResponse.addCardCount(_logic.get_card_count_by_index(GRR._cards_index[i]));
				}

			} else {
				// 牌
				tableResponse.addCardCount(_logic.get_card_count_by_index(GRR._cards_index[i]));

			}

		}

		// 数据
		tableResponse
				.setSendCardData(((_send_card_data != GameConstants.INVALID_VALUE) && (_provide_player == seat_index))
						? _send_card_data : GameConstants.INVALID_VALUE);
		int hand_cards[] = new int[GameConstants.MAX_COUNT];
		int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[seat_index], hand_cards);

		if (_status_send == true) {
			// 牌
			if (seat_index == _current_player) {
				_logic.remove_card_by_data(hand_cards, _send_card_data);
			}
		}

		for (int i = 0; i < GameConstants.MAX_COUNT; i++) {
			tableResponse.addCardsData(hand_cards[i]);
		}

		roomResponse.setTable(tableResponse);

		this.send_response_to_player(seat_index, roomResponse);

		if (_status_send == true) {
			// 牌
			this.operate_player_get_card(_current_player, 1, new int[] { _send_card_data }, seat_index);
		} else {
			if (_out_card_player != GameConstants.INVALID_SEAT && _out_card_data != GameConstants.INVALID_VALUE) {
				this.operate_out_card(_out_card_player, 1, new int[] { _out_card_data },
						GameConstants.OUT_CARD_TYPE_MID, seat_index);
			} else if (_status_cs_gang == true) {
				this.operate_out_card(this._provide_player, 2, this._gang_card_data.get_cards(),
						GameConstants.OUT_CARD_TYPE_MID, seat_index);
			}
		}

		if (_playerStatus[seat_index].has_action()) {
			this.operate_player_action(seat_index, false);
		}

		return true;

	}

	private PlayerResultResponse.Builder process_player_result() {

		// 大赢家
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			_player_result.win_order[i] = -1;
		}
		int win_idx = 0;
		float max_score = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			int winner = -1;
			float s = -999999;
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				if (_player_result.win_order[j] != -1) {
					continue;
				}
				if (_player_result.game_score[j] > s) {
					s = _player_result.game_score[j];
					winner = j;
				}
			}
			if (s >= max_score) {
				max_score = s;
			} else {
				win_idx++;
			}
			if (winner != -1) {
				_player_result.win_order[winner] = win_idx;
				// win_idx++;
			}
		}

		PlayerResultResponse.Builder player_result = PlayerResultResponse.newBuilder();

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			player_result.addGameScore(_player_result.game_score[i]);
			player_result.addWinOrder(_player_result.win_order[i]);

			Int32ArrayResponse.Builder lfs = Int32ArrayResponse.newBuilder();
			for (int j = 0; j < GameConstants.GAME_PLAYER; j++) {
				lfs.addItem(_player_result.lost_fan_shu[i][j]);
			}

			player_result.addLostFanShu(lfs);

			// if(is_mj_type(MJGameConstants.GAME_TYPE_ZZ) ||
			// is_mj_type(MJGameConstants.GAME_TYPE_HZ)||
			// is_mj_type(MJGameConstants.GAME_TYPE_SHUANGGUI)){
			player_result.addZiMoCount(_player_result.zi_mo_count[i]);
			player_result.addJiePaoCount(_player_result.jie_pao_count[i]);
			player_result.addDianPaoCount(_player_result.dian_pao_count[i]);
			player_result.addAnGangCount(_player_result.an_gang_count[i]);
			player_result.addMingGangCount(_player_result.ming_gang_count[i]);
			// }else if(is_mj_type(MJGameConstants.GAME_TYPE_CS)||
			// is_mj_type(MJGameConstants.GAME_TYPE_ZHUZHOU)){
			player_result.addDaHuZiMo(_player_result.da_hu_zi_mo[i]);
			player_result.addDaHuJiePao(_player_result.da_hu_jie_pao[i]);
			player_result.addDaHuDianPao(_player_result.da_hu_dian_pao[i]);
			player_result.addXiaoHuZiMo(_player_result.xiao_hu_zi_mo[i]);
			player_result.addXiaoHuJiePao(_player_result.xiao_hu_jie_pao[i]);
			player_result.addXiaoHuDianPao(_player_result.xiao_hu_dian_pao[i]);

			player_result.addPiaoLaiCount(_player_result.piao_lai_count[i]);
			// }
		}

		player_result.setRoomId(this.getRoom_id());
		player_result.setRoomOwnerAccountId(this.getRoom_owner_account_id());
		player_result.setRoomOwnerName(this.getRoom_owner_name());
		player_result.setCreateTime(this.getCreate_time());
		player_result.setRecordId(this.get_record_id());
		player_result.setGameRound(_game_round);
		player_result.setGameRuleDes(get_game_des());
		player_result.setGameRuleIndex(_game_rule_index);
		player_result.setGameTypeIndex(_game_type_index);
		return player_result;
	}

	/**
	 * 加载基础状态 癞子 定鬼 牌型状态 玩家状态
	 * 
	 * @param roomResponse
	 */
	public void load_common_status(RoomResponse.Builder roomResponse) {
		roomResponse.setCurrentPlayer(_current_player == GameConstants.INVALID_SEAT ? _resume_player : _current_player);
		if (GRR != null) {
			roomResponse.setLeftCardCount(GRR._left_card_count);
			for (int i = 0; i < GRR._especial_card_count; i++) {
				if (_logic.is_ding_gui_card(GRR._especial_show_cards[i])) {
					roomResponse.addEspecialShowCards(
							GRR._especial_show_cards[i] + GameConstants.CARD_ESPECIAL_TYPE_DING_GUI);
				} else if (_logic.is_lai_gen_card(GRR._especial_show_cards[i])) {
					roomResponse.addEspecialShowCards(
							GRR._especial_show_cards[i] + GameConstants.CARD_ESPECIAL_TYPE_LAI_GEN);
				} else {
					roomResponse.addEspecialShowCards(GRR._especial_show_cards[i]);
				}

			}

			if (GRR._especial_txt != "") {
				roomResponse.setEspecialTxt(GRR._especial_txt);
				roomResponse.setEspecialTxtType(GRR._especial_txt_type);
			}
		}
		roomResponse.setGameStatus(_game_status);

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			roomResponse.addCardStatus(_playerStatus[i]._card_status);
			roomResponse.addPlayerStatus(_playerStatus[i].get_status());
		}
	}

	/***
	 * 加载房间的玩法 状态信息
	 * 
	 * @param roomResponse
	 */
	public void load_room_info_data(RoomResponse.Builder roomResponse) {
		RoomInfo.Builder room_info = RoomInfo.newBuilder();
		room_info.setRoomId(this.getRoom_id());
		room_info.setGameRuleIndex(_game_rule_index);
		room_info.setGameRuleDes(get_game_des());
		room_info.setGameTypeIndex(_game_type_index);
		room_info.setGameRound(_game_round);
		room_info.setCurRound(_cur_round);
		room_info.setGameStatus(_game_status);
		room_info.setCreatePlayerId(this.getRoom_owner_account_id());
		roomResponse.setRoomInfo(room_info);
	}

	/**
	 * 加载房间里的玩家信息
	 * 
	 * @param roomResponse
	 */
	public void load_player_info_data(RoomResponse.Builder roomResponse) {
		Player rplayer;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			rplayer = this.get_players()[i];
			if (rplayer == null)
				continue;
			RoomPlayerResponse.Builder room_player = RoomPlayerResponse.newBuilder();
			room_player.setAccountId(rplayer.getAccount_id());
			room_player.setHeadImgUrl(rplayer.getAccount_icon());
			room_player.setIp(rplayer.getAccount_ip());
			room_player.setUserName(rplayer.getNick_name());
			room_player.setSeatIndex(rplayer.get_seat_index());
			room_player.setOnline(rplayer.isOnline() ? 1 : 0);
			room_player.setIpAddr(rplayer.getAccount_ip_addr());
			room_player.setSex(rplayer.getSex());
			room_player.setScore(_player_result.game_score[i]);
			room_player.setReady(_player_ready[i]);
			room_player.setPao(_player_result.pao[i]);
			room_player.setQiang(_player_result.qiang[i]);
			if(rplayer.locationInfor!=null) {
				room_player.setLocationInfor(rplayer.locationInfor);
			}
			roomResponse.addPlayers(room_player);
		}
	}

	public boolean send_response_to_player(int seat_index, RoomResponse.Builder roomResponse) {

		roomResponse.setRoomId(super.getRoom_id());// 日志用的
		if (this.get_players()[seat_index] == null) {
			return false;
		}
		Response.Builder responseBuilder = Response.newBuilder();
		responseBuilder.setResponseType(ResponseType.ROOM);
		responseBuilder.setExtension(Protocol.roomResponse, roomResponse.build());
		PlayerServiceImpl.getInstance().send(this.get_players()[seat_index], responseBuilder.build());
		return true;
	}

	public boolean send_sys_response_to_player(int seat_index, String msg) {
		if (this.get_players()[seat_index] == null) {
			return false;
		}

		Response.Builder responseBuilder = Response.newBuilder();
		responseBuilder.setResponseType(ResponseType.MSG);
		MsgAllResponse.Builder msgBuilder = MsgAllResponse.newBuilder();
		msgBuilder.setType(ESysMsgType.NONE.getId());
		msgBuilder.setMsg(msg);
		msgBuilder.setErrorId(EMsgIdType.ROOM_ERROR.getId());
		responseBuilder.setExtension(Protocol.msgAllResponse, msgBuilder.build());
		PlayerServiceImpl.getInstance().send(this.get_players()[seat_index], responseBuilder.build());
		return true;
	}

	public boolean send_response_to_room(RoomResponse.Builder roomResponse) {

		roomResponse.setRoomId(super.getRoom_id());// 日志用的
		Player player = null;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			player = this.get_players()[i];
			if (player == null)
				continue;

			Response.Builder responseBuilder = Response.newBuilder();
			responseBuilder.setResponseType(ResponseType.ROOM);
			responseBuilder.setExtension(Protocol.roomResponse, roomResponse.build());

			PlayerServiceImpl.getInstance().send(this.get_players()[i], responseBuilder.build());
		}

		return true;
	}

	public boolean send_error_notify(int seat_index, int type, String msg) {
		MsgAllResponse.Builder e = MsgAllResponse.newBuilder();
		e.setType(type);
		e.setMsg(msg);
		Response.Builder responseBuilder = Response.newBuilder();
		responseBuilder.setResponseType(ResponseType.MSG);
		responseBuilder.setExtension(Protocol.msgAllResponse, e.build());
		PlayerServiceImpl.getInstance().send(this.get_players()[seat_index], responseBuilder.build());

		return false;

	}

	public boolean send_response_to_other(int seat_index, RoomResponse.Builder roomResponse) {

		roomResponse.setRoomId(super.getRoom_id());// 日志用的
		Player player = null;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			player = this.get_players()[i];
			if (player == null)
				continue;
			if (i == seat_index)
				continue;
			Response.Builder responseBuilder = Response.newBuilder();
			responseBuilder.setResponseType(ResponseType.ROOM);
			responseBuilder.setExtension(Protocol.roomResponse, roomResponse.build());

			PlayerServiceImpl.getInstance().send(this.get_players()[i], responseBuilder.build());
		}

		return true;
	}

	private void progress_banker_select() {
		if (_banker_select == GameConstants.INVALID_SEAT) {
			_banker_select = 0;// 创建者的玩家为专家

			// Random random = new Random();//
			// int rand = random.nextInt(6) + 1 + random.nextInt(6) + 1;
			// _banker_select = rand % MJGameConstants.GAME_PLAYER;//
			// ((lSiceCount>>24)+(lSiceCount>>16)-1)%MJGameConstants.GAME_PLAYER;

			_shang_zhuang_player = GameConstants.INVALID_SEAT;
			_lian_zhuang_player = GameConstants.INVALID_SEAT;
		}
	}

	private void record_game_room() {
		// 第一局开始
		_gameRoomRecord = new GameRoomRecord();

		this.set_record_id(BrandIdDict.getInstance().getId());

		_gameRoomRecord.set_record_id(this.get_record_id());
		_gameRoomRecord.setRoom_id(this.getRoom_id());
		_gameRoomRecord.setRoom_owner_account_id(this.getRoom_owner_account_id());
		_gameRoomRecord.setCreate_time(this.getCreate_time());
		_gameRoomRecord.setRoom_owner_name(this.getRoom_owner_name());
		_gameRoomRecord.set_player(_player_result);
		_gameRoomRecord.setPlayers(this.get_players());

		_gameRoomRecord.request_player_seat = GameConstants.INVALID_SEAT;

		_gameRoomRecord.setGame_id(this.getGame_id());
		// 设置战绩游戏ID
		// if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {
		// _gameRoomRecord.setGame_id(GameConstants.GAME_ID_HUBEI);
		// } else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY) ||
		// is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)
		// || is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
		// _gameRoomRecord.setGame_id(GameConstants.GAME_ID_HENAN);
		// } else if (is_mj_type(GameConstants.GAME_TYPE_SXG)) {
		// _gameRoomRecord.setGame_id(GameConstants.GAME_ID_HENGYANG);
		// } else {
		// _gameRoomRecord.setGame_id(GameConstants.GAME_ID_HUNAN);
		// }

		_recordRoomRecord = MongoDBServiceImpl.getInstance().parentBrand(_gameRoomRecord.getGame_id(),
				this.get_record_id(), "", _gameRoomRecord.to_json(), (long) this._game_round,
				(long) this._game_type_index, this.getRoom_id() + "");

		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			MongoDBServiceImpl.getInstance().accountBrand(_gameRoomRecord.getGame_id(),
					this.get_players()[i].getAccount_id(), this.get_record_id());
		}

	}

	public boolean is_mj_type(int type) {
		return _game_type_index == type;
	}

	public boolean is_mj(int id) {
		return this.getGame_id() == id;
	}

	private void record_game_round(GameEndResponse.Builder game_end) {
		if (_recordRoomRecord != null) {
			_recordRoomRecord.setMsg(_gameRoomRecord.to_json());
			MongoDBServiceImpl.getInstance().updateParenBrand(_recordRoomRecord);
		}

		if (GRR != null) {
			game_end.setRecord(GRR.get_video_record());
			long id = BrandIdDict.getInstance().getId();
			game_end.setBrandId(id);

			GameEndResponse ge = game_end.build();

			byte[] gzipByte = ZipUtil.gZip(ge.toByteArray());

			// 记录 to mangodb
			MongoDBServiceImpl.getInstance().childBrand(_gameRoomRecord.getGame_id(), id, this.get_record_id(), "",
					null, null, gzipByte, this.getRoom_id() + "",
					_recordRoomRecord == null ? "" : _recordRoomRecord.getBeginArray());

		}
		
		if((cost_dou>0) && (this._cur_round==1)){
			//不是正常结束的
			if((game_end.getEndType()!=GameConstants.Game_End_NORMAL) && (game_end.getEndType()!=GameConstants.Game_End_DRAW)){
				//还豆
				StringBuilder buf = new StringBuilder();
				buf.append("开局失败["+game_end.getEndType()+"]"+":" + this.getRoom_id()).append("game_id:" + this.getGame_id())
						.append(",game_type_index:" + _game_type_index).append(",game_round:" + _game_round).append(",房主:" + this.getRoom_owner_account_id()).append(",豆+:" + cost_dou);
				//把豆还给玩家
				AddGoldResultModel result = PlayerServiceImpl.getInstance().addGold(this.getRoom_owner_account_id(),
						cost_dou, false, buf.toString(), EGoldOperateType.FAILED_ROOM);
				if(result.isSuccess()==false){
					logger.error("房间[" + this.getRoom_id() + "]"+"玩家["+this.getRoom_owner_account_id()+"]还豆失败");
				}
				
			}
		}
		
		

	}

	public void log_error(String error) {

		logger.error("房间[" + this.getRoom_id() + "]" + error);

	}

	public void log_player_error(int seat_index, String error) {

		logger.error("房间[" + this.getRoom_id() + "]" + " 玩家[" + seat_index + "]" + error);

	}

	private void set_result_descibe_zz() {
		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					}
					if (type == GameConstants.CHR_ZI_MO) {
						des += " 自摸";
						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_HUNAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}

			GRR._result_des[i] = des;
		}
	}

	private void set_result_descibe_hz() {
		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					}
					if (type == GameConstants.CHR_ZI_MO) {
						if (!(GRR._chi_hu_rights[i].opr_and(GameConstants.CHR_HUNAN_HZ_QISHOU_HU).is_empty())) {
							des += " 起手自摸";
						} else {
							des += " 自摸";
						}

						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_HUNAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}

			GRR._result_des[i] = des;
		}
	}

	private void set_result_descibe_hnhz() {
		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					}
					if (type == GameConstants.CHR_ZI_MO) {
						if (!(GRR._chi_hu_rights[i].opr_and(GameConstants.CHR_HENAN_HZ_QISHOU_HU).is_empty())) {
							des += " 起手自摸";
						} else {
							des += " 自摸";
						}

						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_HENAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}

			GRR._result_des[i] = des;
		}
	}

	private void set_result_describe_sg() {
		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					}
					if (type == GameConstants.CHR_ZI_MO) {
						des += " 自摸";
						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
						if (GRR._count_pick_niao > 0) {
							des += " 中鸟X" + GRR._count_pick_niao;
						}
					}
					if (type == GameConstants.CHR_HUNAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}

			GRR._result_des[i] = des;
		}
	}

	private void set_result_describe_xthh() {
		int l;
		long type = 0;
		// int hjh = this.hei_jia_hei();
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					} else if (type == GameConstants.CHR_HUBEI_HEI_MO) {
						des += " 黑摸";
					} else if (type == GameConstants.CHR_HUBEI_RUAN_MO) {
						des += " 软摸";
					} else if (type == GameConstants.CHR_HUBEI_ZHUO_CHONG) {
						des += " 捉铳";
					} else if (type == GameConstants.CHR_HUBEI_RE_CHONG) {
						des += " 热铳";
					} else if (type == GameConstants.CHR_HUBEI_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 放铳";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放铳";
					}
				}
			}
			int meng_xiao = 0, dian_xiao = 0, hui_tou_xiao = 0, xiao_chao_tian = 0, da_chao_tian = 0, fang_xiao = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (p == i) {// 自己
							if (GRR._weave_items[p][w].weave_kind == GameConstants.WIK_MENG_XIAO) {
								meng_xiao++;
							} else if (GRR._weave_items[p][w].weave_kind == GameConstants.WIK_DIAN_XIAO) {
								dian_xiao++;
							} else if (GRR._weave_items[p][w].weave_kind == GameConstants.WIK_HUI_TOU_XIAO) {
								hui_tou_xiao++;
							} else if (GRR._weave_items[p][w].weave_kind == GameConstants.WIK_XIAO_CHAO_TIAN) {
								xiao_chao_tian++;
							} else if (GRR._weave_items[p][w].weave_kind == GameConstants.WIK_DA_CHAO_TIAN) {
								da_chao_tian++;
							} else {
							}
						} else {
							// 放杠笑
							if ((GRR._weave_items[p][w].weave_kind == GameConstants.WIK_DIAN_XIAO
									|| GRR._weave_items[p][w].weave_kind == GameConstants.WIK_XIAO_CHAO_TIAN)
									&& GRR._weave_items[p][w].provide_player == i) {
								fang_xiao++;
							}

						}

					}
				}
			}

			if (meng_xiao > 0) {
				des += " 闷笑X" + meng_xiao;
			}
			if (dian_xiao > 0) {
				des += " 点笑X" + dian_xiao;
			}
			if (hui_tou_xiao > 0) {
				des += " 回头笑X" + hui_tou_xiao;
			}
			if (xiao_chao_tian > 0) {
				des += " 小朝天";
			}
			if (da_chao_tian > 0) {
				des += " 大朝天";
			}
			if (fang_xiao > 0) {
				des += " 放笑X" + fang_xiao;
			}

			// if(hjh == i){
			// des+=" 黑加黑";
			// }

			int piao_lai_cout = 0;
			for (int j = 0; j < GRR._piao_lai_count; j++) {
				if (GRR._piao_lai_seat[j] == i) {
					piao_lai_cout++;
				}
			}
			if (piao_lai_cout > 0) {
				des += " 飘赖*" + piao_lai_cout;
			}

			GRR._result_des[i] = des;
		}
	}

	/**
	 * 安阳麻将结算描述
	 */
	private void set_result_describe_ay() {
		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_ZI_MO) {
						des += " 自摸";
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
					}
					if (type == GameConstants.CHR_HENAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}

					if (type == GameConstants.CHR_HENAN_DAN_DIAO) {
						des += " 单吊";
					} else if (type == GameConstants.CHR_HENAN_KA_ZHANG) {
						des += " 卡张";
					} else if (type == GameConstants.CHR_HENAN_BIAN_ZHANG) {
						des += " 边张";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			if (GRR._chi_hu_rights[i].hua_count > 0) {
				des += " 财神";
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}

			GRR._result_des[i] = des;
		}
	}

	/**
	 * 林州麻将结算描述
	 */
	private void set_result_describe_lz() {
		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_ZI_MO) {
						des += " 自摸";
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
					}
					if (type == GameConstants.CHR_HENAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
					if (type == GameConstants.CHR_HENAN_TIAN_HU) {
						des += " 天胡";
					}
					if (type == GameConstants.CHR_HENAN_QI_XIAO_DUI) {
						des += " 七小对";
					}
					if (type == GameConstants.CHR_HENAN_HH_QI_XIAO_DUI) {
						des += " 豪华七小对";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}

			GRR._result_des[i] = des;
		}
	}

	/**
	 * 河南麻将结算描述
	 */
	private void set_result_describe_henan() {
		// 杠牌，每个人的分数
		int lGangScore[] = new int[GameConstants.GAME_PLAYER];
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {

			for (int j = 0; j < GRR._gang_score[i].gang_count; j++) {
				for (int k = 0; k < GameConstants.GAME_PLAYER; k++) {
					lGangScore[k] += GRR._gang_score[i].scores[j][k];// 杠牌，每个人的分数
				}
			}
		}

		int l;
		long type = 0;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			if (has_rule(GameConstants.GAME_RULE_HENAN_JIA_DI) && i == GRR._banker_player) {
				des += " 庄家加底";
			}

			// if(has_rule(MJGameConstants.GAME_TYPE_HENAN_GANG_PAO) &&
			// i==GRR._banker_player) {
			// des+=" 杠跑";
			// }

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_ZI_MO) {
						des += " 自摸";
					}

					if (type == GameConstants.CHR_HENAN_QISHOU_HU) {
						des += " 4混加倍";
					}

					if (type == GameConstants.CHR_SHU_FAN) {
						des += " 接炮";
					}
					if (type == GameConstants.CHR_HENAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
					if (type == GameConstants.CHR_HENAN_TIAN_HU) {
						des += " 天胡";
					}
					if (type == GameConstants.CHR_HENAN_QI_XIAO_DUI) {
						if (has_rule(GameConstants.GAME_RULE_HENAN_QIDUI_DOUBLE)) {
							des += " 七小对加倍";
						} else {
							des += " 七小对";
						}
					}
					if (type == GameConstants.CHR_HENAN_HH_QI_XIAO_DUI) {
						des += " 豪华七小对";
					}
					if (type == GameConstants.CHR_HENAN_GANG_KAI) {
						if (has_rule(GameConstants.GAME_RULE_HENAN_GANGHUA_DOUBLE)) {
							des += " 杠上花加倍";
						} else {
							des += " 杠上花";
						}

					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
				}
			}
			int jie_gang = 0, fang_gang = 0, ming_gang = 0, an_gang = 0;
			if (GRR != null) {
				for (int p = 0; p < GameConstants.GAME_PLAYER; p++) {
					for (int w = 0; w < GRR._weave_count[p]; w++) {
						if (GRR._weave_items[p][w].weave_kind != GameConstants.WIK_GANG) {
							continue;
						}
						if (p == i) {// 自己
							// 接杠
							if (GRR._weave_items[p][w].provide_player != p) {
								jie_gang++;
							} else {
								if (GRR._weave_items[p][w].public_card == 1) {// 明杠
									ming_gang++;
								} else {
									an_gang++;
								}
							}
						} else {
							// 放杠
							if (GRR._weave_items[p][w].provide_player == i) {
								fang_gang++;
							}
						}
					}
				}
			}

			if (an_gang > 0) {
				des += " 暗杠X" + an_gang;
			}
			if (ming_gang > 0) {
				des += " 明杠X" + ming_gang;
			}
			if (fang_gang > 0) {
				des += " 放杠X" + fang_gang;
			}
			if (jie_gang > 0) {
				des += " 接杠X" + jie_gang;
			}
			if (lGangScore[i] != 0 && GRR._end_type == GameConstants.Game_End_NORMAL) {
				des += " 杠总分(" + lGangScore[i] + ")";
			}
			GRR._result_des[i] = des;
		}
	}

	// 转转麻将结束描述
	private void set_result_describe() {
		if (this.is_mj_type(GameConstants.GAME_TYPE_ZZ)) {
			set_result_descibe_zz();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			set_result_descibe_hz();
		} else if (this.is_mj_type(GameConstants.GAME_TYPE_CS)) {
			set_result_describe_cs();
		} else if (is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI)) {
			set_result_describe_sg();
		} else if (is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			set_result_describe_zhuzhou();
		} else if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {
			set_result_describe_xthh();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			set_result_describe_ay();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)) {
			set_result_describe_lz();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			set_result_describe_henan();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
			set_result_descibe_hnhz();
		}

	}

	// 长沙麻将结束描述
	private void set_result_describe_cs() {
		int l;
		long type = 0;
		// 有可能是同炮
		boolean has_da_hu = false;
		// 大胡
		boolean dahu[] = { false, false, false, false };
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			if (GRR._chi_hu_rights[i].is_valid() && GRR._chi_hu_rights[i].da_hu_count > 0) {
				dahu[i] = true;
				has_da_hu = true;
			}
		}
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			ChiHuRight chr = GRR._chi_hu_rights[i];
			String des = "";
			// 小胡
			if (GRR._start_hu_right[i].is_valid()) {
				l = GRR._start_hu_right[i].type_count;
				for (int j = 0; j < l; j++) {

					type = GRR._start_hu_right[i].type_list[j];
					if (type == GameConstants.CHR_HUNAN_XIAO_DA_SI_XI) {
						des += " 大四喜";

					}
					if (type == GameConstants.CHR_HUNAN_XIAO_BAN_BAN_HU) {
						des += " 板板胡";

					}
					if (type == GameConstants.CHR_HUNAN_XIAO_QUE_YI_SE) {
						des += " 缺一色";

					}
					if (type == GameConstants.CHR_HUNAN_XIAO_LIU_LIU_SHUN) {
						des += " 六六顺";
					}

				}
			}

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_HUNAN_PENGPENG_HU) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_PENGPENG_HU)) {
							des += " 碰碰胡*2";
						} else {
							des += " 碰碰胡";
						}
					}
					if (type == GameConstants.CHR_HUNAN_JIANGJIANG_HU) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_JIANGJIANG_HU)) {
							des += " 将将胡*2";
						} else {
							des += " 将将胡";
						}
					}
					if (type == GameConstants.CHR_HUNAN_QING_YI_SE) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_QING_YI_SE)) {
							des += " 清一色*2";
						} else {
							des += " 清一色";
						}
					}
					if (type == GameConstants.CHR_HUNAN_HAI_DI_LAO) {
						des += " 海底捞";
					}
					if (type == GameConstants.CHR_HUNAN_HAI_DI_PAO) {
						des += " 海底炮";
					}
					if (type == GameConstants.CHR_HUNAN_QI_XIAO_DUI) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_QI_XIAO_DUI)) {
							des += " 七小对*2";
						} else {
							des += " 七小对";
						}
					}
					if (type == GameConstants.CHR_HUNAN_HAOHUA_QI_XIAO_DUI) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_HAOHUA_QI_XIAO_DUI)) {
							des += " 豪华七小对*2";
						} else {
							des += " 豪华七小对";
						}
					}
					if (type == GameConstants.CHR_HUNAN_SHUANG_HAO_HUA_QI_XIAO_DUI) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_SHUANG_HAO_HUA_QI_XIAO_DUI)) {
							des += " 双豪华七小对*2";
						} else {
							des += " 双豪华七小对";
						}
					}
					if (type == GameConstants.CHR_HUNAN_GANG_KAI) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_GANG_KAI)) {
							des += " 杠上开花*2";
						} else {
							des += " 杠上开花";
						}
					}
					if (type == GameConstants.CHR_HUNAN_SHUANG_GANG_KAI) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_SHUANG_GANG_KAI)) {
							des += " 双杠上开花*2";
						} else {
							des += " 双杠上开花";
						}
					}
					if (type == GameConstants.CHR_HUNAN_QIANG_GANG_HU) {
						des += " 抢杠胡";
					}
					if (type == GameConstants.CHR_HUNAN_GANG_SHANG_PAO) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_GANG_SHANG_PAO)) {
							des += " 杠上炮*2";
						} else {
							des += " 杠上炮";
						}
					}
					if (type == GameConstants.CHR_HUNAN_SHUANG_GANG_SHANG_PAO) {
						if (chr.is_mul(GameConstants.CHR_HUNAN_SHUANG_GANG_SHANG_PAO)) {
							des += " 双杠上炮*2";
						} else {
							des += " 双杠上炮";
						}
					}
					if (type == GameConstants.CHR_HUNAN_QUAN_QIU_REN) {

						des += " 全求人";
					}
					if (type == GameConstants.CHR_ZI_MO) {
						if (dahu[i] == true) {
							des += " 大胡自摸";
						} else {
							des += " 小胡自摸";
						}
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						if (dahu[i] == true) {
							des += " 大胡接炮";
						} else {
							des += " 小胡接炮";
						}
					}
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						if (has_da_hu == true) {
							des += " 大胡放炮";
						} else {
							des += " 小胡放炮";
						}
					}
				}
			}

			if (GRR._player_niao_count[i] > 0) {
				des += " 中鸟X" + GRR._player_niao_count[i];
			}

			GRR._result_des[i] = des;
		}
	}

	// 株洲麻将结束描述
	private void set_result_describe_zhuzhou() {
		int l;
		long type = 0;
		// 有可能是同炮
		boolean has_da_hu = false;
		// 大胡
		boolean dahu[] = { false, false, false, false };
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			if (GRR._chi_hu_rights[i].is_valid() && GRR._chi_hu_rights[i].da_hu_count > 0) {
				dahu[i] = true;
				has_da_hu = true;
			}
		}
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			String des = "";

			l = GRR._chi_hu_rights[i].type_count;
			for (int j = 0; j < l; j++) {
				type = GRR._chi_hu_rights[i].type_list[j];
				if (GRR._chi_hu_rights[i].is_valid()) {
					if (type == GameConstants.CHR_HUNAN_PENGPENG_HU) {
						des += " 碰碰胡";
					}
					// if (type == MJGameConstants.CHR_HUNAN_JIANGJIANG_HU) {
					// des += " 将将胡";
					// }
					if (type == GameConstants.CHR_HUNAN_QING_YI_SE) {
						des += " 清一色";
					}
					if (type == GameConstants.CHR_HUNAN_HAI_DI_LAO) {
						des += " 海底捞";
					}
					if (type == GameConstants.CHR_HUNAN_HAI_DI_PAO) {
						des += " 海底炮";
					}
					if (type == GameConstants.CHR_HUNAN_QI_XIAO_DUI) {
						des += " 七小对";
					}
					if (type == GameConstants.CHR_HUNAN_HAOHUA_QI_XIAO_DUI) {
						des += " 豪华七小对";
					}
					if (type == GameConstants.CHR_HUNAN_SHUANG_HAO_HUA_QI_XIAO_DUI) {
						des += " 双豪华七小对";
					}
					if (type == GameConstants.CHR_HUNAN_GANG_KAI) {
						des += " 杠上开花";
					}
					if (type == GameConstants.CHR_HUNAN_SHUANG_GANG_KAI) {
						des += " 双杠杠上开花";
					}
					// if (type == MJGameConstants.CHR_QIANG_GANG_HU) {
					// des += " 抢杠胡";
					// }
					if (type == GameConstants.CHR_HUNAN_GANG_SHANG_PAO) {
						des += " 杠上炮";
					}
					if (type == GameConstants.CHR_HUNAN_SHUANG_GANG_SHANG_PAO) {
						des += " 双杠上炮";
					}
					// if (type == MJGameConstants.CHR_QUAN_QIU_REN) {
					// des += " 全求人";
					// }
					if (type == GameConstants.CHR_HUNAN_TIAN_HU) {
						des += " 天胡";
					}
					if (type == GameConstants.CHR_HUNAN_DI_HU) {
						des += " 地胡";
					}
					if (type == GameConstants.CHR_HUNAN_MEN_QING) {
						des += " 门清";
					}
					if (type == GameConstants.CHR_ZI_MO) {
						if (dahu[i] == true) {
							des += " 大胡自摸";
						} else {
							des += " 小胡自摸";
						}
					}
					if (type == GameConstants.CHR_SHU_FAN) {
						if (dahu[i] == true) {
							des += " 大胡接炮";
						} else {
							des += " 小胡接炮";
						}
					}
					if (type == GameConstants.CHR_FANG_PAO) {
						des += " 放炮";
					}
					if (type == GameConstants.CHR_TONG_PAO) {
						des += " 通炮";
					}
				} else {
					if (type == GameConstants.CHR_FANG_PAO) {
						if (has_da_hu == true) {
							des += " 大胡放炮";
						} else {
							des += " 小胡放炮";
						}
					}
				}
			}

			if (GRR._player_niao_count[i] > 0) {
				des += " 中鸟X" + GRR._player_niao_count[i];
			}

			if (GRR._hu_result[i] == GameConstants.HU_RESULT_FANG_KAN_QUAN_BAO) {
				des += " 放坎全包";
			}

			GRR._result_des[i] = des;
		}
	}

	// public int refresh_bei_shu(){
	// GRR._bei_shu=1;
	// GRR._especial_txt="";
	// if(GRR._piao_lai_count>0){
	// boolean same_player = true;
	// int seat = GRR._piao_lai_seat[0];
	// for(int i = 0; i < GRR._piao_lai_count; i++){
	// GRR._bei_shu*=2;
	// if(seat!=GRR._piao_lai_seat[i]){
	// same_player = false;
	// }
	// }
	//
	// if(same_player == true && GRR._piao_lai_count==4){
	// GRR._bei_shu*=2;
	// return seat;
	// }
	// }
	// return MJGameConstants.INVALID_SEAT;
	// }

	// 株洲是否听牌
	public boolean is_zhuzhou_ting_card(int cards_index[], WeaveItem weaveItem[], int cbWeaveCount) {
		int handcount = _logic.get_card_count_by_index(cards_index);
		if (handcount == 1) {
			for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
				if (cards_index[i] == 0) {
					continue;
				}
				int cbValue = _logic.get_card_value(_logic.switch_to_card_data(i));

				// 单牌统计
				if ((cbValue != 2) && (cbValue != 5) && (cbValue != 8)) {
					return false;
				}
			}
			return true;
		}

		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();
		for (int i = 0; i < GameConstants.MAX_INDEX - 7; i++) {
			int cbCurrentCard = _logic.switch_to_card_data(i);
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_zhuzhou(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO))
				return true;
		}
		return false;
	}

	/**
	 * 检查杠牌后是否换章
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean check_gang_huan_zhang(int mj_type, int seat_index, int card) {
		// 不能换章，需要检测是否改变了听牌
		int gang_card_index = _logic.switch_to_card_index(card);
		int gang_card_count = GRR._cards_index[seat_index][gang_card_index];
		// 假如杠了
		GRR._cards_index[seat_index][gang_card_index] = 0;

		int hu_cards[] = new int[GameConstants.MAX_INDEX];

		// 检查听牌
		int hu_card_count = 0;
		if (mj_type == GameConstants.GAME_TYPE_HENAN_AY) {
			hu_card_count = get_ay_ting_card(seat_index, hu_cards, GRR._cards_index[seat_index],
					GRR._weave_items[seat_index], GRR._weave_count[seat_index]);
		}

		// 还原手牌
		GRR._cards_index[seat_index][gang_card_index] = gang_card_count;

		if (hu_card_count != _playerStatus[seat_index]._hu_card_count) {
			return true;
		} else {
			for (int j = 0; j < hu_card_count; j++) {
				if (_playerStatus[seat_index]._hu_cards[j] != hu_cards[j]) {
					return true;
				}
			}
		}

		return false;
	}

	// 是否听牌
	public boolean is_cs_ting_card(int cards_index[], WeaveItem weaveItem[], int cbWeaveCount) {
		int handcount = _logic.get_card_count_by_index(cards_index);
		if (handcount == 1) {
			// 全求人
			return true;
		}

		// 复制数据
		int cbCardIndexTemp[] = new int[GameConstants.MAX_INDEX];
		for (int i = 0; i < GameConstants.MAX_INDEX; i++) {
			cbCardIndexTemp[i] = cards_index[i];
		}

		ChiHuRight chr = new ChiHuRight();
		for (int i = 0; i < GameConstants.MAX_ZI; i++) {
			chr.set_empty();
			int cbCurrentCard = _logic.switch_to_card_data(i);
			if (GameConstants.WIK_CHI_HU == analyse_chi_hu_card_cs(cbCardIndexTemp, weaveItem, cbWeaveCount,
					cbCurrentCard, chr, GameConstants.HU_CARD_TYPE_ZIMO))
				return true;
		}
		return false;
	}

	public String get_game_des() {
		String des = "";
		// if (_game_round == 8) {
		// des = "局数:8";
		// }else if(_game_round == 16){
		// des = "局数:16";
		// }

		if (is_mj_type(GameConstants.GAME_TYPE_XTHH)) {

			return get_game_des_xthh();
		} else if (is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			return get_game_des_zhuzhou();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_AY)) {
			return get_game_des_ay();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_LZ)) {
			return get_game_des_lz();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN)) {
			return get_game_des_henan();
		} else if (is_mj_type(GameConstants.GAME_TYPE_HENAN_HZ)) {
			return get_game_des_hnhz();
		}

		if ((_game_type_index == GameConstants.GAME_TYPE_ZZ) || is_mj_type(GameConstants.GAME_TYPE_HZ)) {
			if (has_rule(GameConstants.GAME_RULE_HUNAN_ZIMOHU)) {

				des += "自摸胡";
			} else {
				des += "可炮胡(可抢杠胡)";
			}

			if (has_rule(GameConstants.GAME_RULE_HUNAN_QIDUI)) {

				des += "\n" + "可胡七对";
			}

			// if (is_mj_type(MJGameConstants.GAME_TYPE_HZ)) {
			// des += "\n" + "红中癞子";
			// }

			if (has_rule(GameConstants.GAME_RULE_HUNAN_JIANPAOHU)) {
				des += "\n" + "强制胡牌";
			}
			if (has_rule(GameConstants.GAME_RULE_HUNAN_GANG_HU)) {
				des += "\n" + "抢杠胡";
			}

		} else if (_game_type_index == GameConstants.GAME_TYPE_CS) {

		} else if (_game_type_index == GameConstants.GAME_TYPE_ZHUZHOU) {
			if (has_rule(GameConstants.GAME_RULE_HUNAN_SCORE_ADD)) {
				des += "\n" + "加法记分";
			} else if (has_rule(GameConstants.GAME_RULE_HUNAN_SCORE_MUTIP)) {
				des += "\n" + "乘法记分";
			}
		}

		if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO1)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:1个";
			} else {
				des += "抓鸟:1个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO2)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:2个";
			} else {
				des += "抓鸟:2个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO4)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:4个";
			} else {
				des += "抓鸟:4个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO6)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:6个";
			} else {
				des += "抓鸟:6个";
			}
		} else {
			if (des.length() > 3) {
				des += "\n" + "不抓鸟";
			} else {
				des += "不抓鸟";
			}
		}
		return des;
	}

	public String get_game_des_zhuzhou() {
		String des = "";

		if (has_rule(GameConstants.GAME_RULE_HUNAN_QIANGGANGHU)) {
			des += "可炮胡";
		} else {
			des += "自摸胡";
		}

		if (has_rule(GameConstants.GAME_RULE_HUNAN_JIANPAOHU)) {
			des += "\n" + "强制胡牌";
		}

		if (has_rule(GameConstants.GAME_RULE_HUNAN_SCORE_ADD)) {
			des += "\n" + "加法记分";
		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_SCORE_MUTIP)) {
			des += "\n" + "乘法记分";
		}

		if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO1)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:1个";
			} else {
				des += "抓鸟:1个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO2)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:2个";
			} else {
				des += "抓鸟:2个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO4)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:4个";
			} else {
				des += "抓鸟:4个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HUNAN_ZHANIAO6)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:6个";
			} else {
				des += "抓鸟:6个";
			}
		} else {
			if (des.length() > 3) {
				des += "\n" + "不抓鸟";
			} else {
				des += "不抓鸟";
			}
		}
		return des;
	}

	public boolean is_zhuang_xian() {
		if ((GameConstants.GAME_TYPE_ZZ == _game_type_index) || is_mj_type(GameConstants.GAME_TYPE_HZ)
				|| is_mj_type(GameConstants.GAME_TYPE_SHUANGGUI) || is_mj_type(GameConstants.GAME_TYPE_ZHUZHOU)) {
			return false;
		}
		return true;
	}

	private String get_game_des_xthh() {
		String des = "";
		if (has_rule(GameConstants.GAME_RULE_HEBEI_DI_FEN_05)) {

			des += "底分:0.5";
		} else if (has_rule(GameConstants.GAME_RULE_HEBEI_DI_FEN_10)) {
			des += "底分:1";
		} else if (has_rule(GameConstants.GAME_RULE_HEBEI_DI_FEN_20)) {
			des += "底分:2";
		}

		if (has_rule(GameConstants.GAME_RULE_HEBEI_PIAO_LAI_YOU_JIANG)) {
			des += " 飘赖有奖";
		}

		if (has_rule(GameConstants.GAME_RULE_HEBEI_GAN_DENG_YAN)) {

			des += "\n" + "干瞪眼";
		} else if (has_rule(GameConstants.GAME_RULE_HEBEI_YI_LAI_DAO_DI)) {
			des += "\n" + "一赖到底";
		}

		return des;
	}

	private String get_game_des_ay() {
		String des = "";

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
			des += "带风";
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_XIAPAO)) {
			if (des.length() == 0) {
				des += "庄家可下跑";
			} else {
				des += "\n" + "庄家可下跑";
			}
		}
		return des;
	}

	private String get_game_des_lz() {
		String des = "";

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
			des += "带风";
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {
			if (des.length() == 0) {
				des += "带混";
			} else {
				des += "\n" + "带混";
			}
		}
		return des;
	}

	private String get_game_des_hnhz() {
		String des = "";

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
			des += "带风";
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_ZIMOHU)) {

			des += "自摸胡";
		} else {
			des += "可炮胡(可抢杠胡)";
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_ZHANIAO2)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:2个";
			} else {
				des += "抓鸟:2个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HENAN_ZHANIAO4)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:4个";
			} else {
				des += "抓鸟:4个";
			}

		} else if (has_rule(GameConstants.GAME_RULE_HENAN_ZHANIAO6)) {
			if (des.length() > 3) {
				des += "\n" + "抓鸟:6个";
			} else {
				des += "抓鸟:6个";
			}
		} else {
			if (des.length() > 3) {
				des += "\n" + "不抓鸟";
			} else {
				des += "不抓鸟";
			}
		}
		return des;
	}

	private String get_game_des_henan() {
		String des = "";
		boolean has_first = false;
		if (has_rule(GameConstants.GAME_RULE_HENAN_JIA_DI)) {
			des += "庄家加底";
			has_first = true;
		}
		if (has_rule(GameConstants.GAME_RULE_HENAN_GANGHUA_DOUBLE)) {
			if (has_first) {
				des += " 杠上开花加倍";
			} else {
				des += "杠上开花加倍";
			}
			has_first = true;
		}
		if (has_rule(GameConstants.GAME_RULE_HENAN_QIDUI_DOUBLE)) {
			if (has_first) {
				des += " 七对加倍";
			} else {
				des += "七对加倍";
			}
			has_first = true;
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_HUN)) {
			if (has_first) {
				des += " 带混";
			} else {
				des += "带混";
			}
			has_first = true;
		} else {
			// if (has_first) {
			// des += " 不带混";
			// } else {
			// des += "不带混";
			// }
			// has_first = true;
		}
		if (has_rule(GameConstants.GAME_RULE_HENAN_DAIFENG)) {
			if (has_first) {
				des += " 带风";
			} else {
				des += "带风";
			}
			has_first = true;
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_HENAN_PAO_HU)) {
			if (has_first) {
				des += " 点炮胡";
			} else {
				des += "点炮胡";
			}
			has_first = true;
		} else {
			if (has_first) {
				des += " 自摸胡";
			} else {
				des += "自摸胡";
			}
			has_first = true;
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_DAI_PAO)) {
			if (has_first) {
				des += " 带跑";
			} else {
				des += "带跑";
			}
			has_first = true;
		} else {
			// if (has_first) {
			// des += " 不带跑";
			// } else {
			// des += "不带跑";
			// }
			// has_first = true;
		}

		if (has_rule(GameConstants.GAME_RULE_HENAN_GANG_PAO)) {
			if (has_first) {
				des += " 杠跑";
			} else {
				des += "杠跑";
			}
		}

		return des;
	}

	public int get_piao_lai_bei_shu(int seat_index, int target_seat) {
		int count = 1;
		for (int i = 0; i < GRR._piao_lai_count; i++) {
			if ((GRR._piao_lai_seat[i] == seat_index) || (GRR._piao_lai_seat[i] == target_seat)) {
				count *= 2;
			}
		}
		int hjh = this.hei_jia_hei();
		if ((seat_index == hjh) || (target_seat == hjh)) {
			count *= 2;
		}
		return count;

	}

	public int hei_jia_hei() {
		int same_player = GameConstants.INVALID_SEAT;
		if (GRR._piao_lai_count == 4) {
			same_player = GRR._piao_lai_seat[0];
			for (int i = 0; i < GRR._piao_lai_count; i++) {
				if (GRR._piao_lai_seat[i] != same_player) {
					return GameConstants.INVALID_SEAT;
				}
			}
		}
		return same_player;

	}

	/**
	 * 取出实际牌数据
	 * 
	 * @param card
	 * @return
	 */
	public int get_real_card(int card) {
		// 错误断言
		if (card > GameConstants.CARD_ESPECIAL_TYPE_GUI && card < GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_GUI;
		} else if (card > GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI && card < GameConstants.CARD_ESPECIAL_TYPE_DING_GUI) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;

		} else if (card > GameConstants.CARD_ESPECIAL_TYPE_DING_GUI
				&& card < GameConstants.CARD_ESPECIAL_TYPE_LAI_GEN) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_DING_GUI;

		} else if (card > GameConstants.CARD_ESPECIAL_TYPE_LAI_GEN && card < GameConstants.CARD_ESPECIAL_TYPE_TING) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_LAI_GEN;

		} else if (card > GameConstants.CARD_ESPECIAL_TYPE_TING && card < GameConstants.CARD_ESPECIAL_TYPE_BAO_TING) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_TING;

		} else if (card > GameConstants.CARD_ESPECIAL_TYPE_BAO_TING && card < GameConstants.CARD_ESPECIAL_TYPE_HUN) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_BAO_TING;
		} else if (card > GameConstants.CARD_ESPECIAL_TYPE_HUN) {
			card -= GameConstants.CARD_ESPECIAL_TYPE_HUN;
		}

		return card;
	}

	/**
	 * 结束调度
	 * 
	 * @return
	 */
	public boolean exe_finish() {
		this._handler = this._handler_finish;
		this._handler_finish.exe(this);

		return true;
	}

	/**
	 * 
	 * @param seat_index
	 * @return
	 */
	public boolean exe_xiao_hu(int seat_index) {
		// 有小胡
		this._handler = this._handler_xiao_hu;
		this._handler_xiao_hu.reset_status(seat_index);
		this._handler_xiao_hu.exe(this);

		return true;
	}

	public boolean exe_qishou_hongzhong(int seat_index) {
		this._handler = this._handler_qishou_hongzhong;
		this._handler_qishou_hongzhong.reset_status(seat_index);
		this._handler_qishou_hongzhong.exe(this);

		return true;
	}

	public boolean exe_qishou_hun(int seat_index) {
		this._handler = this._handler_qishou_hun;
		this._handler_qishou_hun.reset_status(seat_index);
		this._handler_qishou_hun.exe(this);
		return true;
	}

	/**
	 * //执行发牌 是否延迟
	 * 
	 * @param seat_index
	 * @param delay
	 * @return
	 */
	public boolean exe_dispatch_card(int seat_index, int type, int delay) {

		if (delay > 0) {
			GameSchedule.put(new DispatchCardRunnable(this.getRoom_id(), seat_index, type, false), delay,
					TimeUnit.MILLISECONDS);
		} else {
			// 发牌
			this._handler = this._handler_dispath_card;
			this._handler_dispath_card.reset_status(seat_index, type);
			this._handler.exe(this);
		}

		return true;
	}

	/**
	 * //执行发牌 是否延迟
	 * 
	 * @param seat_index
	 * @param delay
	 * @return
	 */
	public boolean exe_dispatch_last_card(int seat_index, int type, int delay_time) {

		if (delay_time > 0) {
			GameSchedule.put(new DispatchLastCardRunnable(this.getRoom_id(), seat_index, type, false), delay_time,
					TimeUnit.MILLISECONDS);// MJGameConstants.GANG_LAST_CARD_DELAY
		} else {
			// 发牌
			this._handler = this._handler_dispath_last_card;
			this._handler_dispath_last_card.reset_status(seat_index, type);
			this._handler.exe(this);
		}

		return true;
	}

	/**
	 * 
	 * @param seat_index
	 * @param provide_player
	 * @param center_card
	 * @param action
	 * @param type
	 *            //共杠还是明杠
	 * @param self
	 *            自己摸的
	 * @param d
	 *            双杠
	 * @return
	 */
	public boolean exe_gang(int seat_index, int provide_player, int center_card, int action, int type, boolean self,
			boolean d) {
		// 是否有抢杠胡
		this._handler = this._handler_gang;
		this._handler_gang.reset_status(seat_index, provide_player, center_card, action, type, self, d);
		this._handler.exe(this);

		return true;
	}

	/**
	 * 长沙麻将杠牌处理
	 * 
	 * @param seat_index
	 * @param d
	 * @return
	 */
	public boolean exe_gang_cs(int seat_index, boolean d) {
		this._handler = this._handler_gang_cs;
		this._handler_gang_cs.reset_status(seat_index, d);
		this._handler_gang_cs.exe(this);
		return true;
	}

	/**
	 * 株洲麻将杠牌处理
	 * 
	 * @param seat_index
	 * @param d
	 * @return
	 */
	public boolean exe_gang_zhuzhu(int seat_index, boolean d) {
		this._handler = this._handler_gang_zhuzhou;
		this._handler_gang_zhuzhou.reset_status(seat_index, d);
		this._handler_gang_zhuzhou.exe(this);
		return true;
	}

	/**
	 * 海底
	 * 
	 * @param seat_index
	 * @return
	 */
	public boolean exe_hai_di(int start_index, int seat_index) {
		this._handler = this._handler_hai_di;
		this._handler_hai_di.reset_status(start_index, seat_index);
		this._handler_hai_di.exe(this);
		return true;
	}

	/**
	 * 天胡
	 * 
	 * @param seat_index
	 * @return
	 */
	public boolean exe_tian_hu(int seat_index) {
		this._handler = this._handler_tianhu;
		this._handler_tianhu.reset_status(seat_index);
		this._handler_tianhu.exe(this);
		return true;
	}

	/**
	 * 地胡
	 * 
	 * @param seat_index
	 * @return
	 */
	public boolean exe_di_hu(int seat_index, int card) {
		this._handler = this._handler_dihu;
		this._handler_dihu.reset_status(seat_index, card);
		this._handler_dihu.exe(this);
		return true;
	}

	/**
	 * 切换地胡handler
	 * 
	 * @return
	 */
	public boolean exe_di_hu(int seat_index) {
		this._handler = this._handler_dihu;
		this._handler_dihu.reset_status(seat_index);
		return true;
	}

	/**
	 * 要海底
	 * 
	 * @param seat_index
	 * @return
	 */
	public boolean exe_yao_hai_di(int seat_index) {
		this._handler = this._handler_yao_hai_di;
		this._handler_yao_hai_di.reset_status(seat_index);
		this._handler_yao_hai_di.exe(this);
		return true;
	}

	/**
	 * 切换到出牌处理器
	 * 
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean exe_out_card(int seat_index, int card, int type) {
		// 出牌
		this._handler = this._handler_out_card_operate;
		this._handler_out_card_operate.reset_status(seat_index, card, type);
		this._handler.exe(this);

		return true;
	}

	/**
	 * @param seat_index
	 * @param card
	 * @return
	 */
	public boolean exe_hun(int seat_index) {
		// 出牌
		this._handler = this._handler_hun;
		this._handler_hun.reset_status(seat_index);
		this._handler_hun.exe(this);

		return true;
	}

	public boolean exe_out_card_bao_ting(int seat_index, int card, int type) {
		// 出牌
		this._handler = this._handler_out_card_bao_ting;
		this._handler_out_card_bao_ting.reset_status(seat_index, card, type);
		this._handler.exe(this);

		return true;
	}

	public boolean exe_chi_peng(int seat_index, int provider, int action, int card, int type) {
		// 出牌
		this._handler = this._handler_chi_peng;
		this._handler_chi_peng.reset_status(seat_index, _out_card_player, action, card, type);
		this._handler.exe(this);

		return true;
	}

	public boolean exe_jian_pao_hu(int seat_index, int action, int card) {

		GameSchedule.put(new JianPaoHuRunnable(this.getRoom_id(), seat_index, action, card),
				GameConstants.DELAY_JIAN_PAO_HU, TimeUnit.MILLISECONDS);

		return true;
	}

	/**
	 * 调度 发最后4张牌
	 * 
	 * @param cur_player
	 * @param type
	 * @param tail
	 * @return
	 */
	public boolean runnable_dispatch_last_card_data(int cur_player, int type, boolean tail) {
		// 发牌
		this._handler = this._handler_dispath_last_card;
		this._handler_dispath_last_card.reset_status(cur_player, type);
		this._handler.exe(this);

		return true;
	}

	/**
	 * 调度,加入牌堆
	 **/
	public void runnable_add_discard(int seat_index, int card_count, int card_data[], boolean send_client) {
		if (seat_index == GameConstants.INVALID_SEAT) {
			return;
		}
		if (GRR == null)
			return;// 最后一张
		for (int i = 0; i < card_count; i++) {
			GRR._discard_count[seat_index]++;

			GRR._discard_cards[seat_index][GRR._discard_count[seat_index] - 1] = card_data[i];
		}
		if (send_client == true) {
			this.operate_add_discard(seat_index, card_count, card_data);
		}
	}

	/**
	 * 调度,小胡结束
	 **/
	public void runnable_xiao_hu(int seat_index) {
		int cards[] = new int[GameConstants.MAX_COUNT];
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 清除动作
			_playerStatus[i].clean_status();
			if (GRR._start_hu_right[i].is_valid()) {

				// 刷新自己手牌
				int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], cards);
				this.operate_player_cards(i, hand_card_count, cards, 0, null);

				// 去掉 小胡排
				this.operate_show_card(i, GameConstants.Show_Card_XiaoHU, 0, null, GameConstants.INVALID_SEAT);
			}
		}

		_game_status = GameConstants.GS_MJ_PLAY;

		this.exe_dispatch_card(seat_index, GameConstants.WIK_NULL, 0);
	}

	/**
	 * 移除赖根
	 * 
	 * @param seat_index
	 */
	public void runnable_finish_lai_gen(int seat_index) {
		// 去掉
		this.operate_show_card(seat_index, GameConstants.Show_Card_Center, 0, null, GameConstants.INVALID_SEAT);

		// 刷新有癞子的牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			boolean has_lai_zi = false;
			for (int j = 0; j < GameConstants.MAX_INDEX; j++) {
				if (GRR._cards_index[i][j] > 0 && _logic.is_magic_index(j)) {
					has_lai_zi = true;
					break;
				}
			}
			if (has_lai_zi) {
				// 刷新自己手牌
				int cards[] = new int[GameConstants.MAX_COUNT];
				int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], cards);
				for (int j = 0; j < hand_card_count; j++) {
					if (_logic.is_magic_card(cards[j])) {
						cards[j] += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
					}
				}
				this.operate_player_cards(i, hand_card_count, cards, 0, null);
			}
		}

		this.exe_dispatch_card(seat_index, GameConstants.WIK_NULL, 0);
	}

	/**
	 * 显示中间出的牌
	 * 
	 * @param seat_index
	 */
	public void runnable_remove_middle_cards(int seat_index) {
		// 去掉
		this.operate_show_card(seat_index, GameConstants.Show_Card_Center, 0, null, GameConstants.INVALID_SEAT);

		// 刷新有癞子的牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			boolean has_lai_zi = false;
			for (int j = 0; j < GameConstants.MAX_INDEX; j++) {
				if (GRR._cards_index[i][j] > 0 && _logic.is_magic_index(j)) {
					has_lai_zi = true;
					break;
				}
			}
			if (has_lai_zi) {
				// 刷新自己手牌
				int cards[] = new int[GameConstants.MAX_COUNT];
				int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], cards);
				for (int j = 0; j < hand_card_count; j++) {
					if (_logic.is_magic_card(cards[j])) {
						cards[j] += GameConstants.CARD_ESPECIAL_TYPE_GUI;
					}
				}
				this.operate_player_cards(i, hand_card_count, cards, 0, null);
			}
		}

		this.exe_dispatch_card(seat_index, GameConstants.WIK_NULL, 0);
	}

	/**
	 * 显示中间出的牌
	 * 
	 * @param seat_index
	 */
	public void runnable_remove_hun_middle_cards(int seat_index) {
		// 去掉
		this.operate_show_card(seat_index, GameConstants.Show_Card_Center, 0, null, GameConstants.INVALID_SEAT);

		// 刷新有癞子的牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			boolean has_lai_zi = false;
			for (int j = 0; j < GameConstants.MAX_INDEX; j++) {
				if (GRR._cards_index[i][j] > 0 && _logic.is_magic_index(j)) {
					has_lai_zi = true;
					break;
				}
			}
			if (has_lai_zi) {
				// 刷新自己手牌
				int cards[] = new int[GameConstants.MAX_COUNT];
				int hand_card_count = _logic.switch_to_cards_data(GRR._cards_index[i], cards);
				for (int j = 0; j < hand_card_count; j++) {
					if (_logic.is_magic_card(cards[j])) {
						cards[j] += GameConstants.CARD_ESPECIAL_TYPE_HUN;
					}
				}
				this.operate_player_cards(i, hand_card_count, cards, 0, null);
			}
		}

		// 检测听牌
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			this._playerStatus[i]._hu_card_count = this.get_henan_ting_card(this._playerStatus[i]._hu_cards,
					this.GRR._cards_index[i], this.GRR._weave_items[i], this.GRR._weave_count[i]);
			if (this._playerStatus[i]._hu_card_count > 0) {
				this.operate_chi_hu_cards(i, this._playerStatus[i]._hu_card_count, this._playerStatus[i]._hu_cards);
			}
		}

		boolean is_qishou_hu = false;
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			// 起手4个红中
			if (this.GRR._cards_index[i][this._logic.get_magic_card_index(0)] == 4) {

				this._playerStatus[i].add_action(GameConstants.WIK_ZI_MO);
				this._playerStatus[i].add_zi_mo(this._logic.switch_to_card_data(this._logic.get_magic_card_index(0)),
						i);
				this.GRR._chi_hu_rights[i].opr_or(GameConstants.CHR_ZI_MO);
				this.GRR._chi_hu_rights[i].opr_or(GameConstants.CHR_HENAN_QISHOU_HU);
				this.exe_qishou_hun(i);
				is_qishou_hu = true;
				break;
			}
		}
		if (is_qishou_hu == false) {
			this.exe_dispatch_card(this._current_player, GameConstants.WIK_NULL, GameConstants.DELAY_SEND_CARD_DELAY);
		}

		//
		// this.exe_dispatch_card(seat_index,MJGameConstants.WIK_NULL, 0);
	}

	/**
	 * 
	 * @param seat_index
	 * @param type
	 */
	public void runnable_remove_out_cards(int seat_index, int type) {
		// 删掉出来的那张牌
		operate_out_card(seat_index, 0, null, type, GameConstants.INVALID_SEAT);
	}

	public void runnable_ding_gui(int seat_index) {
		_handler_da_dian.reset_status(seat_index);
		_handler_da_dian.exe(this);

	}

	public void runnable_hun(int seat_index) {
		_handler_hun.reset_status(seat_index);
		_handler_hun.exe(this);
	}

	/***
	 * 
	 * @param seat_index
	 */
	public void runnable_start_lai_gen(int seat_index) {
		_handler_lai_gen.reset_status(seat_index);
		_handler_lai_gen.exe(this);

	}

	/***
	 * 强制解散
	 */
	public boolean force_account() {
		if (this._cur_round == 0) {
			RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
			roomResponse.setType(MsgConstants.RESPONSE_FORCE_EXIT);// 直接拉出游戏
			this.send_response_to_room(roomResponse);

			Player player = null;
			for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
				player = this.get_players()[i];
				if (player == null)
					continue;
				send_error_notify(i, 2, "游戏已被系统解散");
			}
			// 删除房间
			PlayerServiceImpl.getInstance().delRoomId(this.getRoom_id());

		} else {
			this.handler_game_finish(GameConstants.INVALID_SEAT, GameConstants.Game_End_RELEASE_SYSTEM);
		}

		return false;
	}

	private void shuffle_players() {
		List<Player> pl = new ArrayList<Player>();
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			pl.add(this.get_players()[i]);
		}

		Collections.shuffle(pl);
		pl.toArray(this.get_players());

		// 重新排下位置
		for (int i = 0; i < GameConstants.GAME_PLAYER; i++) {
			get_players()[i].set_seat_index(i);
		}
	}

	public static void main(String[] args) {
		// int cards[] = new int[1000];
		//
		// String d = cards.toString();
		//
		// byte[] dd = d.getBytes();
		//
		// System.out.println("ddddddddd"+dd.length);
		//
		// for(int i=0;i<100000;i++) {
		// int[] _repertory_card_zz = new int[MJGameConstants.CARD_COUNT_ZZ];
		// MJGameLogic logic = new MJGameLogic();
		// logic.random_card_data(_repertory_card_zz);
		// }
		int value = 0x00000200;
		int value1 = 0x200;

		System.out.println(value);
		System.out.println(value1);

		// 测试骰子
		// Random random=new Random();//
		//
		// for(int i=0; i<1000; i++){
		//
		// int rand=random.nextInt(6)+1;//
		// int lSiceCount =
		// FvMask.make_long(FvMask.make_word(rand,rand),FvMask.make_word(rand,rand));
		// int f =
		// ((lSiceCount>>24)+(lSiceCount>>16)-1)%MJGameConstants.GAME_PLAYER;
		//
		// rand=random.nextInt(MJGameConstants.GAME_PLAYER);//
		// System.out.println("==庄家"+rand);
		// }

		/*
		 * int test[][]={{0,0,0,0},{1,0,0,0},{1,0,0,0},{1,0,0,0}}; int sum[] =
		 * new int[4]; for (int i = 0; i < 4; i++) { for (int j = 0; j < 4; j++)
		 * { sum[i]-=test[i][j]; sum[j] += test[i][j]; } }
		 * 
		 * MJGameLogic test_logic = new MJGameLogic();
		 * test_logic.set_magic_card_index(test_logic.switch_to_card_index(
		 * MJGameConstants.ZZ_MAGIC_CARD)); int cards[] = new int[] { 0x35,
		 * 0x35, 0x01, 0x01, 0x03, 0x04, 0x05, 0x11, 0x11, 0x15, 0x15,
		 * 
		 * 0x29, 0x29 , 0x29};
		 * 
		 * List<AnalyseItem> analyseItemArray = new ArrayList<AnalyseItem>();
		 * int cards_index[] = new int[MJGameConstants.MAX_INDEX];
		 * test_logic.switch_to_cards_index(cards, 0, 14, cards_index);
		 * WeaveItem weaveItem[] = new WeaveItem[1]; boolean bValue =
		 * test_logic.analyse_card(cards_index, weaveItem, 0, analyseItemArray);
		 * if (!bValue) { System.out.println("==玩法" +
		 * MJGameConstants.CHR_QI_XIAO_DUI); } //七小队
		 */
		// int cards[] = new int[] { 0x01, 0x01, 0x01, 0x01, 0x12, 0x12, 0x18,
		// 0x18, 0x23, 0x23, 0x26,
		//
		// 0x26, 0x29 };
		//
		// Integer nGenCount = 0;
		// int cards_index[] = new int[MJGameConstants.MAX_INDEX];
		// test_logic.switch_to_cards_index(cards, 0, 13, cards_index);
		// WeaveItem weaveItem[] = new WeaveItem[1];
		// if (test_logic.is_qi_xiao_dui(cards_index, weaveItem, 0, 0x29)!=0) {
		// if (nGenCount > 0) {
		// System.out.println("==玩法" + MJGameConstants.CHR_HAOHUA_QI_XIAO_DUI);
		// //chiHuRight.opr_or(MJGameConstants.CHR_HAOHUA_QI_XIAO_DUI);
		// } else {
		// //chiHuRight.opr_or(MJGameConstants.CHR_QI_XIAO_DUI);
		// System.out.println("==玩法" + MJGameConstants.CHR_QI_XIAO_DUI);
		// }
		// }
		//
		//
		// PlayerResult _player_result = new PlayerResult();
		//
		// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
		// _player_result.win_order[i] = -1;
		//
		// }
		// _player_result.game_score[0] = 400;
		// _player_result.game_score[1] = 400;
		// _player_result.game_score[2] = 300;
		// _player_result.game_score[3] = 200;
		//
		// int win_idx = 0;
		// int max_score = 0;
		// for (int i = 0; i < MJGameConstants.GAME_PLAYER; i++) {
		// int winner = -1;
		// int s = -999999;
		// for (int j = 0; j < MJGameConstants.GAME_PLAYER; j++) {
		// if (_player_result.win_order[j] != -1) {
		// continue;
		// }
		// if (_player_result.game_score[j] > s) {
		// s = _player_result.game_score[j];
		// winner = j;
		// }
		// }
		// if(s>=max_score){
		// max_score = s;
		// }else{
		// win_idx++;
		// }
		//
		// if (winner != -1) {
		// _player_result.win_order[winner] = win_idx;
		// //win_idx++;
		// }
		// }

		// 测试玩法的
		// int rule_1 = FvMask.mask(MJGameConstants.GAME_TYPE_ZZ_ZIMOHU);
		// int rule_2 = FvMask.mask(MJGameConstants.GAME_TYPE_ZZ_ZHANIAO2);
		// int rule = rule_1 | rule_2;
		// boolean has = FvMask.has_any(rule, rule_2);
		// System.out.println("==玩法" + has);
		//
		// // 测试牌局
		// MJTable table = new MJTable();
		// int game_type_index = MJGameConstants.GAME_TYPE_CS;
		// int game_rule_index = rule_1 | rule_2;//
		// MJGameConstants.GAME_TYPE_ZZ_HONGZHONG
		// int game_round = 8;
		// table.init_table(game_type_index, game_rule_index, game_round);
		// boolean start = table.handler_game_start();
		//
		// for (int i = 0; i < MJGameConstants.MAX_INDEX; i++) {
		// if (table._cards_index[table._current_player][i] > 0) {
		// table.handler_player_out_card(table._current_player,
		// _logic.switch_to_card_data(table._cards_index[table._current_player][i]));
		// break;
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cai.common.domain.Room#getPlayerNumber()
	 */
	@Override
	public int getTablePlayerNumber() {
		return GameConstants.GAME_PLAYER;
	}

	/* (non-Javadoc)
	 * @see com.cai.common.domain.Room#handler_requst_open_less(com.cai.common.domain.Player, boolean)
	 */
	@Override
	public boolean handler_requst_open_less(Player player, boolean openThree) {
		// TODO Auto-generated method stub
		return false;
	}

	// System.out.println("==进入心跳handler==" + start);

	// }

	// public StateMachine<MJTable> get_state_machine() {
	// return _state_machine;
	// }
	//
	// public void set_state_machine(StateMachine<MJTable> _state_machine) {
	// this._state_machine = _state_machine;
	// }

}
