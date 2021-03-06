package com.cai.game.mj.fujianmj;

import java.util.concurrent.TimeUnit;

import com.cai.common.constant.GameConstants;
import com.cai.common.constant.MsgConstants;
import com.cai.common.constant.game.Constants_MJ_GuangZe;
import com.cai.common.domain.ChiHuRight;
import com.cai.common.domain.GangCardResult;
import com.cai.common.domain.PlayerStatus;
import com.cai.future.GameSchedule;
import com.cai.future.runnable.GameFinishRunnable;
import com.cai.game.mj.handler.MJHandlerDispatchCard;

import protobuf.clazz.Protocol.Int32ArrayResponse;
import protobuf.clazz.Protocol.RoomResponse;
import protobuf.clazz.Protocol.TableResponse;
import protobuf.clazz.Protocol.WeaveItemResponse;
import protobuf.clazz.Protocol.WeaveItemResponseArrayResponse;

public class MJHandlerDispatchCard_FuJian_GuangZe extends MJHandlerDispatchCard<MJTable_FuJian_GuangZe> {
	protected GangCardResult m_gangCardResult;
	private boolean ting_send_card = false;
	protected int _type;

	public MJHandlerDispatchCard_FuJian_GuangZe() {
		m_gangCardResult = new GangCardResult();
	}

	public void reset_status(int seat_index, int type) {
		_seat_index = seat_index;
		_type = type;
	}

	@Override
	public void exe(MJTable_FuJian_GuangZe table) {
		// 用户状态 清除
		for (int i = 0; i < table.getTablePlayerNumber(); i++) {
			table._playerStatus[i].clean_action();
			table.change_player_status(i, GameConstants.INVALID_VALUE);
		}

		table._playerStatus[_seat_index].chi_hu_round_valid();// 可以胡了
		table._playerStatus[_seat_index].clear_cards_abandoned_hu();
		table._playerStatus[_seat_index].clear_cards_abandoned_peng();

		// 荒庄结束
		if (table.GRR._left_card_count == table.niao_num) {
			for (int i = 0; i < table.getTablePlayerNumber(); i++) {
				table.GRR._chi_hu_card[i][0] = GameConstants.INVALID_VALUE;
			}
			table._cur_banker = table._cur_banker; // 上一家的庄家继续坐庄
			// 流局
			table.handler_game_finish(table._cur_banker, GameConstants.Game_End_DRAW);

			return;
		}

		PlayerStatus curPlayerStatus = table._playerStatus[_seat_index];
		curPlayerStatus.reset();

		table._current_player = _seat_index; // 轮到操作的人是自己

		// 从牌堆拿出一张牌
		table._send_card_count++;

		_send_card_data = table._repertory_card[table._all_card_len - table.GRR._left_card_count];

		--table.GRR._left_card_count;

		table._provide_player = _seat_index;

		if (table.DEBUG_CARDS_MODE) {
			_send_card_data = 0x06;
		}

		// 记录摸牌次数
		table.addDispatchcardNum(_seat_index);

		// 胡牌判断
		ChiHuRight chr = table.GRR._chi_hu_rights[_seat_index];
		chr.set_empty();

		int action = GameConstants.WIK_NULL;
		// 胡牌检测
		int card_type = Constants_MJ_GuangZe.GAME_RULE_ZIMOHU;
		action = table.analyse_chi_hu_card(table.GRR._cards_index[_seat_index], table.GRR._weave_items[_seat_index],
				table.GRR._weave_count[_seat_index], _send_card_data, chr, card_type, _seat_index, false); // 自摸

		if (action != GameConstants.WIK_NULL) {
			// 添加动作
			curPlayerStatus.add_action(action);
			curPlayerStatus.add_zi_mo(_send_card_data, _seat_index);
			// curPlayerStatus.add_normal_wik(_send_card_data, action,
			// _seat_index);
		} else {
			table.GRR._chi_hu_rights[_seat_index].set_empty();
			chr.set_empty();
		}

		// 加到手牌
		table.GRR._cards_index[_seat_index][table._logic.switch_to_card_index(_send_card_data)]++;

		// TODO: 出任意一张牌时，能胡哪些牌 -- Begin
		int count = 0;
		int ting_count = 0;
		int send_card_index = table._logic.switch_to_card_index(_send_card_data);
		ting_send_card = false;

		int card_type_count = GameConstants.MAX_ZI;

		for (int i = 0; i < card_type_count; i++) {
			count = table.GRR._cards_index[_seat_index][i];

			if (count > 0) {
				table.GRR._cards_index[_seat_index][i]--;

				table._playerStatus[_seat_index]._hu_out_card_ting_count[ting_count] = table.get_ting_card(
						table._playerStatus[_seat_index]._hu_out_cards[ting_count], table.GRR._cards_index[_seat_index],
						table.GRR._weave_items[_seat_index], table.GRR._weave_count[_seat_index], _seat_index);

				if (table._playerStatus[_seat_index]._hu_out_card_ting_count[ting_count] > 0) {
					table._playerStatus[_seat_index]._hu_out_card_ting[ting_count] = table._logic
							.switch_to_card_data(i);

					ting_count++;

					if (send_card_index == i) {
						ting_send_card = true;
					}
				}

				table.GRR._cards_index[_seat_index][i]++;
			}
		}

		table._playerStatus[_seat_index]._hu_out_card_count = ting_count;

		if (ting_count > 0) {
			table.GRR._cards_index[_seat_index][send_card_index]--;

			int cards[] = new int[GameConstants.MAX_COUNT];
			int hand_card_count = table._logic.switch_to_cards_data(table.GRR._cards_index[_seat_index], cards);

			table.GRR._cards_index[_seat_index][send_card_index]++;

			for (int i = 0; i < hand_card_count; i++) {
				for (int j = 0; j < ting_count; j++) {
					if (cards[i] == table._playerStatus[_seat_index]._hu_out_card_ting[j]) {
						// 如果既是听牌 又是癞子的牌
						if (table._logic.is_magic_card(cards[i])) {
							cards[i] += (GameConstants.CARD_ESPECIAL_TYPE_TING
									+ GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI);
						} else {
							cards[i] += GameConstants.CARD_ESPECIAL_TYPE_TING;
						}

						break;
					}
				}
				if (cards[i] < GameConstants.CARD_ESPECIAL_TYPE_TING) {
					if (table._logic.is_magic_card(cards[i]))
						cards[i] += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
				}
			}

			table.operate_player_cards_with_ting(_seat_index, hand_card_count, cards, 0, null);
		}
		// TODO: 出任意一张牌时，能胡哪些牌 -- End

		// 发送数据
		// 只有自己才有数值
		int show_send_card = _send_card_data;
		if (table._logic.is_magic_card(_send_card_data) && ting_send_card) {
			show_send_card = show_send_card + GameConstants.CARD_ESPECIAL_TYPE_TING
					+ GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
		} else if (table._logic.is_magic_card(_send_card_data)) {
			show_send_card += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
		} else if (ting_send_card) {
			show_send_card += GameConstants.CARD_ESPECIAL_TYPE_TING;
		}

		table.operate_player_get_card(_seat_index, 1, new int[] { show_send_card }, GameConstants.INVALID_SEAT);

		// 设置变量
		table._provide_card = _send_card_data; // 提供的牌

		if (table.GRR._left_card_count >= 0) {
			m_gangCardResult.cbCardCount = 0;
			int cbActionMask = table.analyse_gang_exclude_magic_card(table.GRR._cards_index[_seat_index],
					_send_card_data, table.GRR._weave_items[_seat_index], table.GRR._weave_count[_seat_index],
					m_gangCardResult, table._playerStatus[_seat_index].get_cards_abandoned_gang());

			if (cbActionMask != GameConstants.WIK_NULL) { // 有杠
				curPlayerStatus.add_action(GameConstants.WIK_GANG);
				for (int i = 0; i < m_gangCardResult.cbCardCount; i++) {
					// 加上杠
					curPlayerStatus.add_gang(m_gangCardResult.cbCardData[i], _seat_index, m_gangCardResult.isPublic[i]);
				}
			}
		}

		if (curPlayerStatus.has_action()) { // 有动作
			table.change_player_status(_seat_index, GameConstants.Player_Status_OPR_CARD);
			table.operate_player_action(_seat_index, false);
		} else {
			table.change_player_status(_seat_index, GameConstants.Player_Status_OUT_CARD);
			table.operate_player_status();
		}

		return;
	}

	@Override
	public boolean handler_operate_card(MJTable_FuJian_GuangZe table, int seat_index, int operate_code,
			int operate_card) {
		PlayerStatus playerStatus = table._playerStatus[seat_index];

		// 效验操作
		if ((operate_code != GameConstants.WIK_NULL) && (playerStatus.has_action_by_code(operate_code) == false)) {
			table.log_error("没有这个操作");
			return false;
		}

		if (seat_index != _seat_index) {
			table.log_error("不是当前玩家操作");
			return false;
		}

		// 是否已经响应
		if (playerStatus.is_respone()) {
			table.log_player_error(seat_index, "出牌,玩家已操作");
			return true;
		}

		// 记录玩家的操作
		playerStatus.operate(operate_code, operate_card);
		playerStatus.clean_status();

		if ((table._playerStatus[seat_index].has_action_by_code(GameConstants.WIK_GANG))
				&& operate_code != GameConstants.WIK_GANG) {

			if (table.GRR._cards_index[_seat_index][table._logic.switch_to_card_index(_send_card_data)] == 1) {// 碰完之后手上还有一张这个牌
				table._playerStatus[_seat_index]
						.add_cards_abandoned_gang(table._logic.switch_to_card_index(_send_card_data));
			}
		}

		// 放弃操作
		if (operate_code == GameConstants.WIK_NULL) {
			table.record_effect_action(seat_index, GameConstants.EFFECT_ACTION_TYPE_ACTION, 1,
					new long[] { GameConstants.WIK_NULL }, 1);

			// 用户状态
			table._playerStatus[_seat_index].clean_action();
			table._playerStatus[_seat_index].clean_status();

			table.change_player_status(_seat_index, GameConstants.Player_Status_OUT_CARD);
			table.operate_player_status();

			return true;
		}

		// if (operate_code == Constants_MJ_GuangZe.WIK_DI_HU || operate_code ==
		// Constants_MJ_GuangZe.WIK_QI_DA_DUI
		// || operate_code == Constants_MJ_GuangZe.WIK_QI_XIAO_DUI
		// || operate_code == Constants_MJ_GuangZe.WIK_QING_YI_SE
		// || operate_code == Constants_MJ_GuangZe.WIK_DAN_DIAO || operate_code
		// == Constants_MJ_GuangZe.WIK_HHQXD
		// || operate_code == Constants_MJ_GuangZe.WIK_TIAN_HU) {
		// operate_code = GameConstants.WIK_ZI_MO;
		// }

		// 执行动作
		switch (operate_code) {
		case GameConstants.WIK_GANG: // 杠牌操作
		{
			for (int i = 0; i < m_gangCardResult.cbCardCount; i++) {
				if (operate_card == m_gangCardResult.cbCardData[i]) {
					// 是否有抢杠胡
					table.exe_gang(_seat_index, _seat_index, operate_card, operate_code, m_gangCardResult.type[i], true,
							false);
					return true;
				}
			}

		}
			break;
		case GameConstants.WIK_ZI_MO: // 自摸
		{
			table.GRR._chi_hu_rights[_seat_index].set_valid(true);

			table._cur_banker = _seat_index;

			table.GRR._chi_hu_card[_seat_index][0] = operate_card;

			table.set_niao_card(_seat_index, 0);

			table.process_chi_hu_player_operate(_seat_index, operate_card, true);
			table.has_zi_mo = true;
			table.process_chi_hu_player_score(_seat_index, _seat_index, operate_card, true); // 自摸胡算分
			// 记录
			table._player_result.zi_mo_count[_seat_index]++;

			// 结束
			GameSchedule.put(new GameFinishRunnable(table.getRoom_id(), _seat_index, GameConstants.Game_End_NORMAL),
					GameConstants.GAME_FINISH_DELAY, TimeUnit.SECONDS);

			return true;
		}
		}

		return true;
	}

	@Override
	public boolean handler_player_be_in_room(MJTable_FuJian_GuangZe table, int seat_index) {
		RoomResponse.Builder roomResponse = RoomResponse.newBuilder();
		roomResponse.setType(MsgConstants.RESPONSE_RECONNECT_DATA);

		TableResponse.Builder tableResponse = TableResponse.newBuilder();

		table.load_room_info_data(roomResponse);
		table.load_player_info_data(roomResponse);
		table.load_common_status(roomResponse);

		tableResponse.setBankerPlayer(table.GRR._banker_player);
		tableResponse.setCurrentPlayer(_seat_index);
		tableResponse.setCellScore(0);

		tableResponse.setActionCard(0);

		tableResponse.setOutCardData(0);
		tableResponse.setOutCardPlayer(0);

		for (int i = 0; i < table.getTablePlayerNumber(); i++) {
			tableResponse.addTrustee(false);
			tableResponse.addDiscardCount(table.GRR._discard_count[i]);
			Int32ArrayResponse.Builder int_array = Int32ArrayResponse.newBuilder();
			for (int j = 0; j < 55; j++) {
				if (table._logic.is_magic_card(table.GRR._discard_cards[i][j])) {
					// 癞子
					int_array.addItem(table.GRR._discard_cards[i][j] + GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI);
				} else {
					int_array.addItem(table.GRR._discard_cards[i][j]);
				}
			}
			tableResponse.addDiscardCards(int_array);

			tableResponse.addWeaveCount(table.GRR._weave_count[i]);
			WeaveItemResponseArrayResponse.Builder weaveItem_array = WeaveItemResponseArrayResponse.newBuilder();
			for (int j = 0; j < GameConstants.MAX_WEAVE; j++) {
				WeaveItemResponse.Builder weaveItem_item = WeaveItemResponse.newBuilder();
				weaveItem_item.setCenterCard(table.GRR._weave_items[i][j].center_card);
				weaveItem_item.setProvidePlayer(
						table.GRR._weave_items[i][j].provide_player + GameConstants.WEAVE_SHOW_DIRECT);
				weaveItem_item.setPublicCard(table.GRR._weave_items[i][j].public_card);
				weaveItem_item.setWeaveKind(table.GRR._weave_items[i][j].weave_kind);
				weaveItem_array.addWeaveItem(weaveItem_item);
			}
			tableResponse.addWeaveItemArray(weaveItem_array);

			tableResponse.addWinnerOrder(0);

			if (i == _seat_index) {
				tableResponse.addCardCount(table._logic.get_card_count_by_index(table.GRR._cards_index[i]) - 1);
			} else {
				tableResponse.addCardCount(table._logic.get_card_count_by_index(table.GRR._cards_index[i]));
			}
		}

		tableResponse.setSendCardData(0);
		int hand_cards[] = new int[GameConstants.MAX_COUNT];
		int hand_card_count = table._logic.switch_to_cards_data(table.GRR._cards_index[seat_index], hand_cards);

		if (seat_index == _seat_index) {
			table._logic.remove_card_by_data(hand_cards, _send_card_data);
		}

		// TODO: 出任意一张牌时，能胡哪些牌 -- Begin
		int out_ting_count = table._playerStatus[seat_index]._hu_out_card_count;

		if ((out_ting_count > 0) && (seat_index == _seat_index)) {

			for (int j = 0; j < hand_card_count; j++) {
				for (int k = 0; k < out_ting_count; k++) {
					if (hand_cards[j] == table._playerStatus[seat_index]._hu_out_card_ting[k]) {

						// 如果既是听牌 又是癞子的牌
						if (table._logic.is_magic_card(hand_cards[j])) {
							hand_cards[j] += (GameConstants.CARD_ESPECIAL_TYPE_TING
									+ GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI);
						} else {
							hand_cards[j] += GameConstants.CARD_ESPECIAL_TYPE_TING;
						}

						break;
					}
				}
				if (hand_cards[j] < GameConstants.CARD_ESPECIAL_TYPE_TING) {
					if (table._logic.is_magic_card(hand_cards[j]))
						hand_cards[j] += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
				}
			}
		} else {
			for (int j = 0; j < hand_card_count; j++) {
				if (table._logic.is_magic_card(hand_cards[j])) {
					hand_cards[j] += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
				}
			}
		}

		for (int i = 0; i < GameConstants.MAX_COUNT; i++) {
			tableResponse.addCardsData(hand_cards[i]);
		}

		roomResponse.setTable(tableResponse);

		roomResponse.setOutCardCount(out_ting_count);

		for (int i = 0; i < out_ting_count; i++) {
			int ting_card_cout = table._playerStatus[seat_index]._hu_out_card_ting_count[i];
			roomResponse.addOutCardTingCount(ting_card_cout);
			if (table._logic.is_magic_card(table._playerStatus[seat_index]._hu_out_card_ting[i])) {
				roomResponse.addOutCardTing(table._playerStatus[seat_index]._hu_out_card_ting[i]
						+ GameConstants.CARD_ESPECIAL_TYPE_TING + GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI);
			} else {
				roomResponse.addOutCardTing(
						table._playerStatus[seat_index]._hu_out_card_ting[i] + GameConstants.CARD_ESPECIAL_TYPE_TING);
			}

			Int32ArrayResponse.Builder int_array = Int32ArrayResponse.newBuilder();
			for (int j = 0; j < ting_card_cout; j++) {
				int tmp_card = table._playerStatus[seat_index]._hu_out_cards[i][j];
				if (table._logic.is_magic_card(tmp_card)) {
					tmp_card += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
				}
				int_array.addItem(tmp_card);
			}
			roomResponse.addOutCardTingCards(int_array);
		}

		table.send_response_to_player(seat_index, roomResponse);

		for (int i = 0; i < GameConstants.MAX_COUNT; i++) {
			tableResponse.addCardsData(hand_cards[i]);
		}

		roomResponse.setTable(tableResponse);
		table.send_response_to_player(seat_index, roomResponse);

		int ting_cards[] = table._playerStatus[seat_index]._hu_cards;
		int ting_count = table._playerStatus[seat_index]._hu_card_count;

		if (ting_count > 0) {
			table.operate_chi_hu_cards(seat_index, ting_count, ting_cards);
		}

		int show_send_card = _send_card_data;
		if (table._logic.is_magic_card(_send_card_data) && ting_send_card) {
			show_send_card = show_send_card + GameConstants.CARD_ESPECIAL_TYPE_TING
					+ GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
		} else if (table._logic.is_magic_card(_send_card_data)) {
			show_send_card += GameConstants.CARD_ESPECIAL_TYPE_LAI_ZI;
		} else if (ting_send_card) {
			show_send_card += GameConstants.CARD_ESPECIAL_TYPE_TING;
		}
		table.operate_player_get_card(_seat_index, 1, new int[] { show_send_card }, seat_index);

		if (table._playerStatus[seat_index].has_action() && (table._playerStatus[seat_index].is_respone() == false)) {
			table.operate_player_action(seat_index, false);
		}

		return true;
	}
}
