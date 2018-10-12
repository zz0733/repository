package com.cai.game.shengji;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.cai.common.constant.GameConstants;
import com.cai.common.util.RandomUtil;
import com.cai.game.shengji.data.tagAnalyseCardType;
import com.cai.game.shengji.data.tagAnalyseIndexResult_Xpsj;
import com.cai.game.shengji.handler.yz240.SJTable_YZ_240;

public class SJGameLogic_YZSJ extends SJGameLogic {

	public SJGameLogic_YZSJ() {
		_chang_zhu_one = 15;
		_chang_zhu_two = 14;
		_chang_zhu_three = 10;
		_chang_zhu_four = 2;
	}

	// 分析扑克
	public void AnalysebCardDataToIndex(int cbCardData[], int cbCardCount,
			tagAnalyseIndexResult_Xpsj AnalyseIndexResult) {

		for (int i = 0; i < cbCardCount; i++) {
			int index = GetCardLogicValue(cbCardData[i]);
			int color = this.GetCardColor(cbCardData[i]);
			AnalyseIndexResult.card_data[color][index - 3][AnalyseIndexResult.card_index[color][index
					- 3]] = cbCardData[i];
			AnalyseIndexResult.card_index[color][index - 3]++;

		}
	}
	
	public class cardInfo {
		public int cardindex; //牌序号
		public int carddata;  //牌值
		public int cardcount;  //牌数量
		public cardInfo() {
			cardindex = 0;
			carddata = 0;
			cardcount = 0;
		}
		public void reset(){
			cardindex = 0;
			carddata = 0;
			cardcount = 0;
		}
	}
	// 获取类型
	/*
	 * public int GetCardType(int cbCardData[], int cbCardCount) { if
	 * (cbCardCount == 1) { return SJConstants.XP_SJ_CT_SINGLE; }
	 * 
	 * //类型相同 tagAnalyseCardType type_card = new tagAnalyseCardType();
	 * Analyse_card_type(cbCardData, cbCardCount, type_card); for (int i = 1; i
	 * < type_card.type_count; i++) { if (type_card.type[i] !=
	 * type_card.type[0]) { return SJConstants.XP_SJ_CT_ERROR; } }
	 * 
	 * //颜色相同 int color = this.GetCardColor(cbCardData[0]); for (int i = 0; i <
	 * cbCardCount; i++) { if (GetCardColor(cbCardData[i]) != color) { return
	 * SJConstants.XP_SJ_CT_ERROR; } }
	 * 
	 * tagAnalyseIndexResult_Xpsj IndexResult = new
	 * tagAnalyseIndexResult_Xpsj(); AnalysebCardDataToIndex(cbCardData,
	 * cbCardCount, IndexResult); int min_index = -1; int max_index = -1; //对子
	 * if (cbCardCount == 2) { int index = this.get_card_index(cbCardData[0]);
	 * if (IndexResult.card_index[color][index] == 2) { return
	 * SJConstants.XP_SJ_CT_DOUBLE; } }
	 * 
	 * // 先过滤AA22的 if (IndexResult.card_index[color][SJConstants.XP_SJ_MAX_INDEX
	 * - 5] == 2 && IndexResult.card_index[color][SJConstants.XP_SJ_MAX_INDEX -
	 * 4] == 2 && color == this._zhu_type) { if (cbCardCount == 4) { return
	 * SJConstants.XP_SJ_CT_DOUBLE_LINK; } else { return
	 * SJConstants.XP_SJ_CT_ERROR; } } else { // 正常拖拉机 for (int i = 0; i <
	 * SJConstants.XP_SJ_MAX_INDEX; i++) { if (color != this._zhu_type) { if ((i
	 * == 7 || i == 13 || i == 12) && IndexResult.card_index[color][i] > 0) {
	 * return SJConstants.XP_SJ_CT_ERROR; } } if
	 * (IndexResult.card_index[color][i] == 2) { if (min_index == -1) {
	 * min_index = i; max_index = i; } else { max_index = i; } } else if
	 * (IndexResult.card_index[color][i] == 0) { if (min_index != -1) { if
	 * ((max_index - min_index + 1) * 2 == cbCardCount) { return
	 * SJConstants.XP_SJ_CT_DOUBLE_LINK; } else { return
	 * SJConstants.XP_SJ_CT_ERROR; } } } else { return
	 * SJConstants.XP_SJ_CT_ERROR; } } } return
	 * SJConstants.XP_SJ_CT_DOUBLE_LINK; }
	 */

	// 获取类型
	public int GetCardType(int cbCardData[], int cbCardCount) {
		if (cbCardCount == 1) {
			return SJConstants.XP_SJ_CT_SINGLE;
		}
		tagAnalyseCardType type_card = new tagAnalyseCardType();
		Analyse_card_type(cbCardData, cbCardCount, type_card);
		for (int i = 1; i < type_card.type_count; i++) {
			if (type_card.type[i] != type_card.type[0]) {
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}

		int color = this.GetCardColor(cbCardData[0]);

		tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
		AnalysebCardDataToIndex(cbCardData, cbCardCount, IndexResult);
		int min_index = -1;
		int max_index = -1;
		if (cbCardCount == 2) {
			int index = this.get_card_index(cbCardData[0]);
			if (IndexResult.card_index[color][index] == 2) {
				return SJConstants.XP_SJ_CT_DOUBLE;
			}else{
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}

		// 无主，拖拉机
		if (this._zhu_type == 4) {
			boolean bhave_chang_zhu = false;
			if (IndexResult.card_index[color][7] > 0 || IndexResult.card_index[color][12] > 0
					|| IndexResult.card_index[color][13] > 0 || IndexResult.card_index[color][14] > 0) {
				bhave_chang_zhu = true;
			}
			if (bhave_chang_zhu) {//无主常主拖拉机
				return judge_wu_zhu_chang_zhu(cbCardData, cbCardCount);
			} else {//无主无常主拖拉机
				return judge_zheng_chang_card(cbCardData, cbCardCount);
				// 正常拖拉机
				/*for (int i = 0; i < SJConstants.XP_SJ_MAX_INDEX - 2; i++) {
					if ((i == 7 || i == 12 || i == 13) && IndexResult.card_index[color][i] > 0) {
						return SJConstants.XP_SJ_CT_ERROR;
					}
					if (IndexResult.card_index[color][i] == 2) {
						if (min_index == -1) {
							min_index = i;
							max_index = i;
						} else {
							max_index = i;
						}
					} else {
						if (min_index != -1) {
							if ((max_index - min_index + 1) * 2 == cbCardCount) {
								return SJConstants.XP_SJ_CT_DOUBLE_LINK;
							} else {
								return SJConstants.XP_SJ_CT_ERROR;
							}
						}

					}
				}
				return SJConstants.XP_SJ_CT_ERROR;*/
			}
		}

		// 有主拖拉机
		else {
			boolean hava_zhu = false;
			for(int i = 0;i < cbCardCount;i++){
				int card_value = GetCardValue(cbCardData[i]);
				int card_color = GetCardColor(cbCardData[i]);
				if(card_color == _zhu_type || card_value == _chang_zhu_one || card_value == _chang_zhu_two||
						card_value == _chang_zhu_three || card_value == _chang_zhu_four){
					hava_zhu = true;
				}
			}
			if (hava_zhu) {//有主，的主牌拖拉机
				return judge_you_zhu_chang_zhu(cbCardData, cbCardCount);
			} else {//有主，正常拖拉机
				return judge_zheng_chang_card(cbCardData, cbCardCount);
				/*for (int i = 0; i < SJConstants.XP_SJ_MAX_INDEX - 2; i++) {
					if (color != this._zhu_type) {
						if ((i == 7 || i == 13 || i == 12) && IndexResult.card_index[color][i] > 0) {
							return SJConstants.XP_SJ_CT_ERROR;
						}
					}
					if (IndexResult.card_index[color][i] == 2) {
						if (min_index == -1) {
							min_index = i;
							max_index = i;
						} else {
							max_index = i;
						}
					} else {
						if (min_index != -1) {
							if ((max_index - min_index + 1) * 2 == cbCardCount) {
								return SJConstants.XP_SJ_CT_DOUBLE_LINK;
							} else {
								return SJConstants.XP_SJ_CT_ERROR;
							}
						}

					}
				}*/
			}
		}
		//return SJConstants.XP_SJ_CT_ERROR;
	}
	
	// 正常的拖拉机判断，主要是55 77 和 99 jj的拖拉机
	public int judge_zheng_chang_card(int cbCardData[], int cbCardCount) {
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == this._chang_zhu_three
					|| this.GetCardValue(cbCardData[i]) == this._chang_zhu_four
					|| this.GetCardValue(cbCardData[i]) == this._chang_zhu_one
					|| this.GetCardValue(cbCardData[i]) == this._chang_zhu_two) {
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}
		
		List<Integer> list_score_card = new ArrayList<Integer>();
		
		int frist_color = this.GetCardColor(cbCardData[0]);
		for(int i = 0;i < cbCardCount;i++){
			int card_color = GetCardColor(cbCardData[i]);
			if(card_color != frist_color){
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}
		if(has_rule(GameConstants.GAME_RULE_ALL_GAME_THREE_PLAYER)){
			// 添加5
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 5) {
					list_score_card.add(1);
				}
			}
			//添加7
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 7) {
					list_score_card.add(2);
				}
			}
			//添加8
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 8) {
					list_score_card.add(3);
				}
			}
			// 添加9
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 9) {
					list_score_card.add(4);
				}
			}
			//添加j
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 11) {
					list_score_card.add(5);
				}
			}
			//添加q
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 12) {
					list_score_card.add(6);
				}
			}
			//添加k
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 13) {
					list_score_card.add(7);
				}
			}
			//添加a
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 1) {
					list_score_card.add(8);
				}
			}

		}else{
			// 添加5
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 5) {
					list_score_card.add(1);
				}
			}
			//添加6
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 6) {
					list_score_card.add(2);
				}
			}
			//添加7
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 7) {
					list_score_card.add(3);
				}
			}
			//添加8
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 8) {
					list_score_card.add(4);
				}
			}
			// 添加9
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 9) {
					list_score_card.add(5);
				}
			}
			//添加j
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 11) {
					list_score_card.add(6);
				}
			}
			//添加q
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 12) {
					list_score_card.add(7);
				}
			}
			//添加k
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 13) {
					list_score_card.add(8);
				}
			}
			//添加a
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 1) {
					list_score_card.add(9);
				}
			}
		}
		
		list_score_card.sort(Comparator.naturalOrder());// 正序比较
		int frist_card = list_score_card.get(0);
		for (int i = 2; i < list_score_card.size(); i += 2) {
			if (frist_card + i / 2 != list_score_card.get(i)) {
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}

		return SJConstants.XP_SJ_CT_DOUBLE_LINK;
	}

	// 无主的常主牌牌型，分花色
	public int judge_wu_zhu_chang_zhu(int cbCardData[], int cbCardCount) {
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) != this._chang_zhu_three
					&& this.GetCardValue(cbCardData[i]) != this._chang_zhu_four
					&& this.GetCardValue(cbCardData[i]) != this._chang_zhu_one
					&& this.GetCardValue(cbCardData[i]) != this._chang_zhu_two) {
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}
		//
		List<Integer> list_score_card = new ArrayList<Integer>();
		// 添加2
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == 2) {
				int color = this.GetCardColor(cbCardData[i]);
				list_score_card.add(color + 1);
			}
		}
		// 添加10
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == 10) {
				int color = this.GetCardColor(cbCardData[i]);
				list_score_card.add(color + 5);
			}
		}
		// 添加小王
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == 14) {
				//int color = this.GetCardColor(cbCardData[i]);
				list_score_card.add(9);
			}
		}
		// 添加大王
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == 15) {
				//int color = this.GetCardColor(cbCardData[i]);
				list_score_card.add(10);
			}
		}

		list_score_card.sort(Comparator.naturalOrder());// 正序比较
		int frist_card = list_score_card.get(0);
		for (int i = 2; i < list_score_card.size(); i += 2) {
			if (frist_card + i / 2 != list_score_card.get(i)) {
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}

		return SJConstants.XP_SJ_CT_DOUBLE_LINK;
	}

	// 有主的常主牌牌型，不分花色，只分正副
	public int judge_you_zhu_chang_zhu(int cbCardData[], int cbCardCount) {
		
		/*int frist_color = this.GetCardColor(cbCardData[0]);
		for(int i = 0;i < cbCardCount;i++){
			int card_color = GetCardColor(cbCardData[i]);
			int card_value = GetCardColor(cbCardData[i]);
			if(card_value == _chang_zhu_one || card_value == _chang_zhu_two||
					card_value == _chang_zhu_three||card_value == _chang_zhu_four){
				continue;
			}
			if(card_color != frist_color){
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}*/
		
		List<Integer> list_score_card = new ArrayList<Integer>();
		if(has_rule(GameConstants.GAME_RULE_ALL_GAME_THREE_PLAYER)){
			// 添加5
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 5) {
					list_score_card.add(1);
				}
			}
			//添加7
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 7) {
					list_score_card.add(2);
				}
			}
			//添加8
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 8) {
					list_score_card.add(3);
				}
			}
			// 添加9
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 9) {
					list_score_card.add(4);
				}
			}
			//添加j
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 11) {
					list_score_card.add(5);
				}
			}
			//添加q
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 12) {
					list_score_card.add(6);
				}
			}
			//添加k
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 13) {
					list_score_card.add(7);
				}
			}
			//添加a
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 1) {
					list_score_card.add(8);
				}
			}
			
			// 添加2
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 2) {
					int color = this.GetCardColor(cbCardData[i]);
					if (color != this._zhu_type)
						list_score_card.add(9);
					else
						list_score_card.add(10);
				}
			}
			// 添加10
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 10) {
					int color = this.GetCardColor(cbCardData[i]);
					if (color != this._zhu_type)
						list_score_card.add(11);
					else
						list_score_card.add(12);
				}
			}
			// 添加小王
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 14) {
					list_score_card.add(13);
				}
			}
			// 添加大王
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 15) {
					list_score_card.add(14);
				}
			}

		}else{
			// 添加5
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 5) {
					list_score_card.add(1);
				}
			}
			//添加6
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 6) {
					list_score_card.add(2);
				}
			}
			//添加7
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 7) {
					list_score_card.add(3);
				}
			}
			//添加8
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 8) {
					list_score_card.add(4);
				}
			}
			// 添加9
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 9) {
					list_score_card.add(5);
				}
			}
			//添加j
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 11) {
					list_score_card.add(6);
				}
			}
			//添加q
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 12) {
					list_score_card.add(7);
				}
			}
			//添加k
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 13) {
					list_score_card.add(8);
				}
			}
			//添加a
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 1) {
					list_score_card.add(9);
				}
			}
			
			// 添加2
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 2) {
					int color = this.GetCardColor(cbCardData[i]);
					if (color != this._zhu_type)
						list_score_card.add(10);
					else
						list_score_card.add(11);
				}
			}
			// 添加10
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 10) {
					int color = this.GetCardColor(cbCardData[i]);
					if (color != this._zhu_type)
						list_score_card.add(12);
					else
						list_score_card.add(13);
				}
			}
			// 添加小王
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 14) {
					list_score_card.add(14);
				}
			}
			// 添加大王
			for (int i = 0; i < cbCardCount; i++) {
				if (this.GetCardValue(cbCardData[i]) == 15) {
					list_score_card.add(15);
				}
			}
		}

		list_score_card.sort(Comparator.naturalOrder());// 正序比较
		int frist_card = list_score_card.get(0);
		for (int i = 2; i < list_score_card.size(); i += 2) {
			if (frist_card + i / 2 != list_score_card.get(i)) {
				return SJConstants.XP_SJ_CT_ERROR;
			}
		}

		return SJConstants.XP_SJ_CT_DOUBLE_LINK;
	}

	public int player_can_out_card_first(int hand_card_data[], int cbHandCardCount, int card_data[],
			SJTable_YZ_240 table) {
		int can_out_count = 0;
		if (this._zhu_type == 4) {
			/*
			 * if (has_rule(GameConstants.GAME_RULE_XPSJ_NO_ZHU_DA_CHANG_ZHU)) {
			 * for (int i = 0; i < cbHandCardCount; i++) { if
			 * (this.GetCardValue(hand_card_data[i]) == this._chang_zhu_three ||
			 * this.GetCardValue(hand_card_data[i]) == this._chang_zhu_four ||
			 * this.GetCardValue(hand_card_data[i]) == this._chang_zhu_one ||
			 * this.GetCardValue(hand_card_data[i]) == this._chang_zhu_two) {
			 * card_data[can_out_count++] = hand_card_data[i]; } } } else
			 */ {
				tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
				this.AnalysebCardDataToIndex(hand_card_data, cbHandCardCount, IndexResult);

				for (int i = 0; i < table._call_banker_card_count; i++) {
					card_data[can_out_count++] = table._call_baker_data[i] + 0x1000;
				}
				int color = this.GetCardColor(table._call_baker_data[0]);
				int index = this.get_card_index(table._call_baker_data[0]);
				if (IndexResult.card_index[color][index - 1] > 1) {
					for (int j = 0; j < IndexResult.card_index[color][index - 1]; j++) {
						card_data[can_out_count++] = IndexResult.card_data[color][index - 1][j];
					}
				}
			}
		} else {
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(hand_card_data, cbHandCardCount, IndexResult);
			int color = this.GetCardColor(table._call_baker_data[0]);
			int index = this.get_card_index(table._call_baker_data[0]);
			
			for (int i = index + 1; i < 12; i++) {
				if (IndexResult.card_index[color][i] > 1) {
					for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
						card_data[can_out_count++] = IndexResult.card_data[color][i][j];
					}
				} else {
					break;
				}
			}
			for (int i = index - 1; i >= 0; i--) {
				if (IndexResult.card_index[color][i] > 1) {
					for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
						card_data[can_out_count++] = IndexResult.card_data[color][i][j];
					}
				} else {
					break;
				}
			}
			for (int i = 0; i < table._call_banker_card_count; i++) {
				card_data[can_out_count++] = table._call_baker_data[i] + 0x1000;
			}
		}

		return can_out_count;
	}
	
	public int player_can_out_card_first_yz(int hand_card_data[], int cbHandCardCount, int card_data[],
			SJTable_YZ_240 table) {
		int can_out_count = 0;
		if (this._zhu_type == 4) {
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(hand_card_data, cbHandCardCount, IndexResult);

			for (int i = 0; i < table._call_banker_card_count; i++) {
				card_data[can_out_count++] = table._call_baker_data[i] + 0x1000;
			}
			//大王
			if(GetCardValue(table._call_baker_data[0]) == 15){
				//添加小王
				int xiaocount = 0;
				int xiaotemp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 14){
						xiaocount++;
						xiaotemp = hand_card_data[i];
					} 
				}
				if(xiaocount == 2){
					card_data[can_out_count++] = xiaotemp;
					card_data[can_out_count++] = xiaotemp;
				}else{
					return can_out_count;
				}
				//添加黑10
				int hei10count = 0;
				int hei10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 3){
						hei10count++;
						hei10temp = hand_card_data[i];
					} 
				}
				if(hei10count == 2){
					card_data[can_out_count++] = hei10temp;
					card_data[can_out_count++] = hei10temp;
				}else{
					return can_out_count;
				}
				//添加红10
				int hong10count = 0;
				int hong10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 2){
						hong10count++;
						hong10temp = hand_card_data[i];
					} 
				}
				if(hong10count == 2){
					card_data[can_out_count++] = hong10temp;
					card_data[can_out_count++] = hong10temp;
				}
				else{
					return can_out_count;
				}
				
				//添加梅10
				int mei10count = 0;
				int mei10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 1){
						mei10count++;
						mei10temp = hand_card_data[i];
					} 
				}
				if(mei10count == 2){
					card_data[can_out_count++] = mei10temp;
					card_data[can_out_count++] = mei10temp;
				}else{
					return can_out_count;
				}
				//添加方10
				int fang10count = 0;
				int fang10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 0){
						fang10count++;
						fang10temp = hand_card_data[i];
					} 
				}
				if(fang10count == 2){
					card_data[can_out_count++] = fang10temp;
					card_data[can_out_count++] = fang10temp;
				}
				else{
					return can_out_count;
				}
				
				//添加黑2
				int hei2count = 0;
				int hei2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 3){
						hei2count++;
						hei2temp = hand_card_data[i];
					} 
				}
				if(hei2count == 2){
					card_data[can_out_count++] = hei2temp;
					card_data[can_out_count++] = hei2temp;
				}
				else{
					return can_out_count;
				}
				//添加红2
				int hong2count = 0;
				int hong2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 2){
						hong2count++;
						hong2temp = hand_card_data[i];
					} 
				}
				if(hong2count == 2){
					card_data[can_out_count++] = hong2temp;
					card_data[can_out_count++] = hong2temp;
				}else{
					return can_out_count;
				}
				//添加梅2
				int mei2count = 0;
				int mei2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 1){
						mei2count++;
						mei2temp = hand_card_data[i];
					} 
				}
				if(mei2count == 2){
					card_data[can_out_count++] = mei2temp;
					card_data[can_out_count++] = mei2temp;
				}else{
					return can_out_count;
				}
				//添加方2
				int fang2count = 0;
				int fang2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 0){
						fang2count++;
						fang2temp = hand_card_data[i];
					} 
				}
				if(fang2count == 2){
					card_data[can_out_count++] = fang2temp;
					card_data[can_out_count++] = fang2temp;
				}else{
					return can_out_count;
				}
				
			}else{//小王
				//添加大王
				int xiaocount = 0;
				int xiaotemp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 15){
						xiaocount++;
						xiaotemp = hand_card_data[i];
					} 
				}
				if(xiaocount == 2){
					card_data[can_out_count++] = xiaotemp;
					card_data[can_out_count++] = xiaotemp;
				}
				//添加黑10
				int hei10count = 0;
				int hei10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 3){
						hei10count++;
						hei10temp = hand_card_data[i];
					} 
				}
				if(hei10count == 2){
					card_data[can_out_count++] = hei10temp;
					card_data[can_out_count++] = hei10temp;
				}else{
					return can_out_count;
				}
				//添加红10
				int hong10count = 0;
				int hong10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 2){
						hong10count++;
						hong10temp = hand_card_data[i];
					} 
				}
				if(hong10count == 2){
					card_data[can_out_count++] = hong10temp;
					card_data[can_out_count++] = hong10temp;
				}
				else{
					return can_out_count;
				}
				
				//添加梅10
				int mei10count = 0;
				int mei10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 1){
						mei10count++;
						mei10temp = hand_card_data[i];
					} 
				}
				if(mei10count == 2){
					card_data[can_out_count++] = mei10temp;
					card_data[can_out_count++] = mei10temp;
				}else{
					return can_out_count;
				}
				//添加方10
				int fang10count = 0;
				int fang10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == 0){
						fang10count++;
						fang10temp = hand_card_data[i];
					} 
				}
				if(fang10count == 2){
					card_data[can_out_count++] = fang10temp;
					card_data[can_out_count++] = fang10temp;
				}else{
					return can_out_count;
				}
				
				//添加黑2
				int hei2count = 0;
				int hei2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 3){
						hei2count++;
						hei2temp = hand_card_data[i];
					} 
				}
				if(hei2count == 2){
					card_data[can_out_count++] = hei2temp;
					card_data[can_out_count++] = hei2temp;
				}else{
					return can_out_count;
				}
				//添加红2
				int hong2count = 0;
				int hong2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 2){
						hong2count++;
						hong2temp = hand_card_data[i];
					} 
				}
				if(hong2count == 2){
					card_data[can_out_count++] = hong2temp;
					card_data[can_out_count++] = hong2temp;
				}else{
					return can_out_count;
				}
				
				//添加梅2
				int mei2count = 0;
				int mei2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 1){
						mei2count++;
						mei2temp = hand_card_data[i];
					} 
				}
				if(mei2count == 2){
					card_data[can_out_count++] = mei2temp;
					card_data[can_out_count++] = mei2temp;
				}else{
					return can_out_count;
				}
				//添加方2
				int fang2count = 0;
				int fang2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == 0){
						fang2count++;
						fang2temp = hand_card_data[i];
					} 
				}
				if(fang2count == 2){
					card_data[can_out_count++] = fang2temp;
					card_data[can_out_count++] = fang2temp;
				}else{
					return can_out_count;
				}
			}
			
		} else {
			//添加 主10
			for (int i = 0; i < table._call_banker_card_count; i++) {
				card_data[can_out_count++] = table._call_baker_data[i] + 0x1000;
			}
			
			//添加小王
			int xiaocount = 0;
			int xiaotemp = -1;
			boolean have_xiao = false;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 15){
					xiaocount++;
					xiaotemp = hand_card_data[i];
				} 
			}
			if(xiaocount == 2){
				have_xiao = true;
				card_data[can_out_count++] = xiaotemp;
				card_data[can_out_count++] = xiaotemp;
			}
			
			//添加大王
			int dacount = 0;
			int datemp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 15){
					dacount++;
					datemp = hand_card_data[i];
				} 
			}
			if(dacount == 2 && have_xiao){
				card_data[can_out_count++] = datemp;
				card_data[can_out_count++] = datemp;
			}
			
			//添加副10
			int fu10count = 0;
			int fu10temp = -1;
			boolean fu10 = false;
			for(int colo = 0;colo < 4;colo++){
				if(colo == this._zhu_type)
					continue;
				fu10count = 0;
				fu10temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 10 && GetCardColor(hand_card_data[i]) == colo){
						fu10count++;
						fu10temp = hand_card_data[i];
					} 
				}
				if(fu10count == 2){
					fu10 = true;
					card_data[can_out_count++] = fu10temp;
					card_data[can_out_count++] = fu10temp;
				}
			}
			if(!fu10){
				return can_out_count;
			}
			
			//添加正2
			int zheng2count = 0;
			int zheng2temp = -1;
			boolean zheng2 = false;
			for(int colo = 0;colo < 4;colo++){
				if(colo != this._zhu_type)
					continue;
				zheng2count = 0;
				zheng2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == colo){
						zheng2count++;
						zheng2temp = hand_card_data[i];
					} 
				}
				if(zheng2count == 2){
					zheng2 = true;
					card_data[can_out_count++] = zheng2temp;
					card_data[can_out_count++] = zheng2temp;
				}
			}
			if(!zheng2){
				return can_out_count;
			}
			
			//添加副2
			int fu2count = 0;
			int fu2temp = -1;
			boolean fu2 = false;
			for(int colo = 0;colo < 4;colo++){
				if(colo != this._zhu_type)
					continue;
				fu2count = 0;
				fu2temp = -1;
				for(int i = 0;i < cbHandCardCount;i++){
					if(GetCardValue(hand_card_data[i]) == 2 && GetCardColor(hand_card_data[i]) == colo){
						fu2count++;
						fu2temp = hand_card_data[i];
					} 
				}
				if(fu2count == 2){
					fu2 = true;
					card_data[can_out_count++] = fu2temp;
					card_data[can_out_count++] = fu2temp;
				}
			}
			if(!fu2){
				return can_out_count;
			}
			
			//添加主A
			int zhuAcount = 0;
			int zhuAtemp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 1){
					zhuAcount++;
					zhuAtemp = hand_card_data[i];
				} 
			}
			if(zhuAcount == 2 ){
				card_data[can_out_count++] = zhuAtemp;
				card_data[can_out_count++] = zhuAtemp;
			}else{
				return can_out_count;
			}
			
			//添加主K
			int zhuKcount = 0;
			int zhuKtemp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 13){
					zhuKcount++;
					zhuKtemp = hand_card_data[i];
				} 
			}
			if(zhuKcount == 2 ){
				card_data[can_out_count++] = zhuKtemp;
				card_data[can_out_count++] = zhuKtemp;
			}else{
				return can_out_count;
			}
			
			//添加主Q
			int zhuQcount = 0;
			int zhuQtemp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 12){
					zhuQcount++;
					zhuQtemp = hand_card_data[i];
				} 
			}
			if(zhuQcount == 2 ){
				card_data[can_out_count++] = zhuQtemp;
				card_data[can_out_count++] = zhuQtemp;
			}else{
				return can_out_count;
			}
			
			//添加主J
			int zhuJcount = 0;
			int zhuJtemp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 11){
					zhuJcount++;
					zhuJtemp = hand_card_data[i];
				} 
			}
			if(zhuJcount == 2 ){
				card_data[can_out_count++] = zhuJtemp;
				card_data[can_out_count++] = zhuJtemp;
			}else{
				return can_out_count;
			}
			
			//添加主9
			int zhu9count = 0;
			int zhu9temp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 9){
					zhu9count++;
					zhu9temp = hand_card_data[i];
				} 
			}
			if(zhu9count == 2 ){
				card_data[can_out_count++] = zhu9temp;
				card_data[can_out_count++] = zhu9temp;
			}else{
				return can_out_count;
			}
			
			//添加主8
			int zhu8count = 0;
			int zhu8temp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 8){
					zhu8count++;
					zhu8temp = hand_card_data[i];
				} 
			}
			if(zhu8count == 2 ){
				card_data[can_out_count++] = zhu8temp;
				card_data[can_out_count++] = zhu8temp;
			}else{
				return can_out_count;
			}
			
			
			//添加主7
			int zhu7count = 0;
			int zhu7temp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 7){
					zhu7count++;
					zhu7temp = hand_card_data[i];
				} 
			}
			if(zhu7count == 2 ){
				card_data[can_out_count++] = zhu7temp;
				card_data[can_out_count++] = zhu7temp;
			}else{
				return can_out_count;
			}
			
			//添加主6
			int zhu6count = 0;
			int zhu6temp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 6){
					zhu6count++;
					zhu6temp = hand_card_data[i];
				} 
			}
			if(zhu6count == 2 ){
				card_data[can_out_count++] = zhu6temp;
				card_data[can_out_count++] = zhu6temp;
			}else{
				return can_out_count;
			}
			
			//添加主5
			int zhu5count = 0;
			int zhu5temp = -1;
			for(int i = 0;i < cbHandCardCount;i++){
				if(GetCardValue(hand_card_data[i]) == 6){
					zhu5count++;
					zhu5temp = hand_card_data[i];
				} 
			}
			if(zhu5count == 2 ){
				card_data[can_out_count++] = zhu5temp;
				card_data[can_out_count++] = zhu5temp;
			}else{
				return can_out_count;
			}
			
		}

		return can_out_count;
	}
	
	public void getcardinfo(int hand_card_data[], int cbHandCardCount,List<cardInfo> list_card){
		tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
		this.AnalysebCardDataToIndex(hand_card_data, cbHandCardCount, IndexResult);
		cardInfo Info = new cardInfo();
		if(this._zhu_type == 4){
			for(int i = 0; i < cbHandCardCount;i++){
				Info.reset();
				int card_value = this.GetCardValue(hand_card_data[i]);
				int card_color = this.GetCardColor(hand_card_data[i]);
				if(card_value == 2){
					//Info.
				}else if(card_value == 10){
					
				}else if(card_value == 14){
					
				}else if(card_value == 15){
					
				}
			}
		}else{
			
		}

	}


	// 有足够相同数量花色的牌
	public int enough_color(int hand_card_data[], int cbHandCardCount, int cbOutCardData[], int out_card_count,
			int card_data[], int turn_card_count, int turn_card_data[], int must_out_data[], int must_out_count[],
			int turn_score, boolean is_5_must_A, boolean is_first_out, int color, int first_type) {
		int card_out_count = 0;
		int can_out_card_data_temp[] = new int[cbHandCardCount];
		int can_out_card_count_temp = 0;
		for (int i = 0; i < cbHandCardCount; i++) {
			if (color == _zhu_type) {
				if (hand_card_data[i] > this._zhu_value) {
					can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
				}
			} else {
				if (this.GetCardColor(hand_card_data[i]) == color && (hand_card_data[i] < this._zhu_value)) {
					can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
				}
			}
		}

		if (first_type == SJConstants.XP_SJ_CT_SINGLE) {
			if (is_5_must_A) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 判断分牌能不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 1) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						return card_out_count;
					}
				}
			}
			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					int temp[] = new int[turn_card_count];
					temp[0] = can_out_card_data_temp[i];
					if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				}
				if (card_out_count > 0) {
					return card_out_count;
				}
			}
			for (int i = 0; i < can_out_card_count_temp; i++) {
				// 判断分牌能不能出
				if (this.GetCardValue(can_out_card_data_temp[i]) == 5
						|| this.GetCardValue(can_out_card_data_temp[i]) == 13
						|| this.GetCardValue(can_out_card_data_temp[i]) == 10
						|| this.GetCardValue(can_out_card_data_temp[i]) == 14
						|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
					int temp[] = new int[turn_card_count];
					temp[0] = can_out_card_data_temp[i];
					if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				} else {
					card_data[card_out_count++] = can_out_card_data_temp[i];
					can_out_card_data_temp[i] = 0;
				}
			}

			if (card_out_count == 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						return card_out_count;
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if ((this.GetCardValue(can_out_card_data_temp[i]) == 13
							|| this.GetCardValue(can_out_card_data_temp[i]) == 10
							|| this.GetCardValue(can_out_card_data_temp[i]) == 14
							|| this.GetCardValue(can_out_card_data_temp[i]) == 15) && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
				if (card_out_count > 0) {
					return card_out_count;
				}
			}
		} else if (first_type == SJConstants.XP_SJ_CT_DOUBLE) {
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);
			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.comparecarddata(turn_card_data, turn_card_count,
								IndexResult.card_data[card_color][index], 2)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}

				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
			}
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);

				// 先不取分牌
				if (IndexResult.card_index[card_color][index] >= 2) {
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5
							|| this.GetCardValue(can_out_card_data_temp[i]) == 13
							|| this.GetCardValue(can_out_card_data_temp[i]) == 10
							|| this.GetCardValue(can_out_card_data_temp[i]) == 14) {
						if (this.comparecarddata(turn_card_data, turn_card_count,
								IndexResult.card_data[card_color][index], 2)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					} else {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				}
			}
			if (card_out_count >= out_card_count) {
				return card_out_count;
			}
			if (card_out_count < out_card_count) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 只有分牌的情况，捡小的出
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < card_out_count; i++) {
					must_out_data[must_out_count[0]++] = card_data[i];
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先不要分牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先不要分牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先5
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
							if (card_out_count == out_card_count) {
								return card_out_count;
							}
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先5
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10
								|| this.GetCardValue(can_out_card_data_temp[i]) == 13
								|| this.GetCardValue(can_out_card_data_temp[i]) == 14
								|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
							card_data[card_out_count++] = can_out_card_data_temp[i];

						}
					}
				}

			}
		} else {

			int link_count = out_card_count / 2;
			int can_double_count = 0;
			int out_max_index = this.get_card_index(turn_card_data[0]);
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);
				if (IndexResult.card_index[card_color][index] >= 2) {
					can_double_count++;
					i++;
				}

			}
			if (can_double_count >= link_count) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 先不取分牌
					if (IndexResult.card_index[card_color][index] >= 2) {
						boolean is_link = true;
						for (int next_index = index; next_index > index - link_count; next_index--) {
							if (IndexResult.card_index[card_color][next_index] < 2 && index - next_index < link_count) {
								is_link = false;
								break;
							}
						}
						if (is_link && index > out_max_index) {
							int compare_data[] = new int[out_card_count];
							int compare_count = 0;
							for (int next_index = index; next_index > index - link_count; next_index--) {
								for (int j = 0; j < IndexResult.card_index[card_color][next_index]; j++) {
									compare_data[compare_count++] = IndexResult.card_data[card_color][next_index][j];
								}
							}
							if (this.comparecarddata(turn_card_data, turn_card_count, compare_data, out_card_count)) {
								for (int j = 0; j < compare_count; j++) {
									card_data[card_out_count++] = compare_data[j];
								}
								for (int next_index = index; next_index > index - link_count; next_index--) {
									IndexResult.card_index[card_color][next_index] = 0;
								}
								continue;
							}
						}
					}
				}
			}

			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
			}

			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);
				// 先不取分牌
				if (IndexResult.card_index[card_color][index] >= 2) {
					if (this.GetCardValue(can_out_card_data_temp[i]) != 5
							&& this.GetCardValue(can_out_card_data_temp[i]) != 13
							&& this.GetCardValue(can_out_card_data_temp[i]) != 10
							&& this.GetCardValue(can_out_card_data_temp[i]) != 14
							&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}

				}
			}
			if (card_out_count < out_card_count) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 先不取分牌
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}

					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 只有分牌的情况，捡小的出
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (can_double_count < link_count) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (can_double_count < link_count) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (can_double_count < link_count) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (can_double_count < link_count) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}

					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 5
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							if (card_out_count == out_card_count) {
								return card_out_count;
							}
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 5
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (card_out_count == out_card_count) {
							}
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (card_out_count == out_card_count) {
							}
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (card_out_count == out_card_count) {
							}
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							if (card_out_count == out_card_count) {
							}
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
			}
		}

		return card_out_count;

	}

	public int not_enough_color(int hand_card_data[], int cbHandCardCount, int cbOutCardData[], int out_card_count,
			int card_data[], int turn_card_count, int turn_card_data[], int must_out_data[], int must_out_count[],
			int turn_score, boolean is_5_must_A, boolean is_first_out, int color, int first_type) {

		int card_out_count = 0;
		int can_out_card_data_temp[] = new int[cbHandCardCount];
		int can_out_card_count_temp = 0;
		for (int i = 0; i < cbHandCardCount; i++) {
			can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
		}
		// 单牌
		if (first_type == SJConstants.XP_SJ_CT_SINGLE) {
			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					int temp[] = new int[turn_card_count];
					temp[0] = can_out_card_data_temp[i];
					if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
				if (card_out_count > 0) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 判断分牌能不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) != 5
							&& this.GetCardValue(can_out_card_data_temp[i]) != 13
							&& this.GetCardValue(can_out_card_data_temp[i]) != 10
							&& this.GetCardValue(can_out_card_data_temp[i]) != 14
							&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
						int temp[] = new int[turn_card_count];
						temp[0] = can_out_card_data_temp[i];
						if (!this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
			} else {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 判断分牌能不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5
							|| this.GetCardValue(can_out_card_data_temp[i]) == 13
							|| this.GetCardValue(can_out_card_data_temp[i]) == 10
							|| this.GetCardValue(can_out_card_data_temp[i]) == 14
							|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
						int temp[] = new int[turn_card_count];
						temp[0] = can_out_card_data_temp[i];
						if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					} else {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
			}

			if (card_out_count == 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
					if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
					if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
					if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
			}
		} else if (first_type == SJConstants.XP_SJ_CT_DOUBLE) {
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (color == _zhu_type) {
					if (this.GetCardColor(can_out_card_data_temp[i]) == color
							|| this.GetCardValue(hand_card_data[i]) == _chang_zhu_one
							|| GetCardValue(can_out_card_data_temp[i]) == _chang_zhu_two
							|| this.GetCardValue(can_out_card_data_temp[i]) == _chang_zhu_three
							|| GetCardValue(can_out_card_data_temp[i]) == _chang_zhu_four) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				} else {
					if (this.GetCardColor(can_out_card_data_temp[i]) == color
							&& GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_one
							&& GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_two
							&& this.GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_three
							&& GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_four) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				}
			}
			if (card_out_count > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 分牌不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) != 5
							&& this.GetCardValue(can_out_card_data_temp[i]) != 13
							&& this.GetCardValue(can_out_card_data_temp[i]) != 10
							&& this.GetCardValue(can_out_card_data_temp[i]) != 14
							&& this.GetCardValue(can_out_card_data_temp[i]) != 15 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
					if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
					if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
					if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}
				return card_out_count;
			} else {
				tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
				this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);
				if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);

						// 先不取分牌
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.comparecarddata(turn_card_data, turn_card_count,
									IndexResult.card_data[card_color][index], 2)) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
				} else {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);

						// 先不取分牌
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 5
									|| this.GetCardValue(can_out_card_data_temp[i]) == 13
									|| this.GetCardValue(can_out_card_data_temp[i]) == 10
									|| this.GetCardValue(can_out_card_data_temp[i]) == 14
									|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								if (this.comparecarddata(turn_card_data, turn_card_count,
										IndexResult.card_data[card_color][index], 2)) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
									can_out_card_data_temp[i] = 0;
								}
							}
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (this.GetCardValue(can_out_card_data_temp[i]) != 5
							&& this.GetCardValue(can_out_card_data_temp[i]) != 13
							&& this.GetCardValue(can_out_card_data_temp[i]) != 10
							&& this.GetCardValue(can_out_card_data_temp[i]) != 14
							&& this.GetCardValue(can_out_card_data_temp[i]) != 15 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}

				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				if (card_out_count < out_card_count) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
				return card_out_count;
			}
		} else {
			int link_count = out_card_count / 2;
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (color == _zhu_type) {
					if (this.GetCardColor(can_out_card_data_temp[i]) == color
							|| can_out_card_data_temp[i] > this._zhu_value) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				} else {
					if (this.GetCardColor(can_out_card_data_temp[i]) == color
							&& can_out_card_data_temp[i] < this._zhu_value) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				}
			}
			if (card_out_count > 0 || color == _zhu_type) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 分牌不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) != 5
							&& this.GetCardValue(can_out_card_data_temp[i]) != 13
							&& this.GetCardValue(can_out_card_data_temp[i]) != 10
							&& this.GetCardValue(can_out_card_data_temp[i]) != 14
							&& this.GetCardValue(can_out_card_data_temp[i]) != 15 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];

					}
				}
				if (card_out_count < out_card_count) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}

					int other_must_out_data[] = new int[can_out_card_count_temp];
					int other_must_out_count = 0;
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							other_must_out_data[other_must_out_count++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					for (int i = 0; i < other_must_out_count; i++) {
						must_out_data[must_out_count[0]++] = other_must_out_data[i];
					}
					// 都是分牌
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
			} else {
				int can_double_count = 0;
				boolean is_can_guan = false;
				tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
				this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);

				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 先不取分牌
					if (IndexResult.card_index[card_color][index] >= 2 && can_out_card_data_temp[i] > this._zhu_value) {
						boolean is_link = true;
						for (int next_index = index; next_index > index - link_count; next_index--) {
							if (IndexResult.card_index[card_color][next_index] < 2 || 4 == this._zhu_type) {
								is_link = false;
								break;
							}
						}
						if (is_link) {
							int compare_data[] = new int[cbHandCardCount];
							int compare_count = 0;
							for (int next_index = index; next_index > index - link_count; next_index--) {
								for (int j = 0; j < IndexResult.card_index[card_color][next_index]; j++) {
									compare_data[compare_count++] = IndexResult.card_data[card_color][next_index][j];
								}
							}
							if (this.comparecarddata(turn_card_data, turn_card_count, compare_data, out_card_count)) {
								for (int j = 0; j < compare_count; j++) {
									card_data[card_out_count++] = compare_data[j];
								}
								for (int next_index = index; next_index > index - link_count; next_index--) {
									IndexResult.card_index[card_color][next_index] = 0;
								}
								is_can_guan = true;
								continue;
							}
						}
					}
				}

				if (is_can_guan && this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
					return card_out_count;
				}

				int no_score_count = 0;
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] > 0) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							no_score_count++;
						}
					}

				}
				if (no_score_count < out_card_count && card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] > 0) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}

				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] > 0) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}

				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] > 0) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}

				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] > 0) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}

				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] > 0) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
						if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}

				}
			}
		}
		return card_out_count;

	}

	public int player_can_out_card_yz(int hand_card_data[], int cbHandCardCount, int cbOutCardData[],
			int out_card_count, int card_data[], int turn_card_count, int turn_card_data[], int must_out_data[],
			int must_out_count[], int turn_score, boolean is_5_must_A, boolean is_first_out) {
		int card_out_count = 0;
		// 首出
		if (out_card_count == 0) {
			for (int i = 0; i < cbHandCardCount; i++) {
				card_data[card_out_count++] = hand_card_data[i];
			}
			return card_out_count;
		}

		int first_type = this.GetCardType(cbOutCardData, out_card_count);
		int color = this.GetCardColor(cbOutCardData[0]);
		if (GetCardValue(cbOutCardData[0]) == _chang_zhu_three || GetCardValue(cbOutCardData[0]) == _chang_zhu_four
				|| GetCardValue(cbOutCardData[0]) == _chang_zhu_one
				|| GetCardValue(cbOutCardData[0]) == _chang_zhu_two) {
			color = this._zhu_type;
		}

		// 手上同花色的牌数量大于出牌数量
		if (GetCardColor_Count(hand_card_data, cbHandCardCount, color) >= out_card_count) {
			return enough_color(hand_card_data, cbHandCardCount, cbOutCardData, out_card_count, card_data,
					turn_card_count, turn_card_data, must_out_data, must_out_count, turn_score, is_5_must_A,
					is_first_out, color, first_type);
		}

		// 相同花色的牌数量不够
		else {
			return not_enough_color(hand_card_data, cbHandCardCount, cbOutCardData, out_card_count, card_data,
					turn_card_count, turn_card_data, must_out_data, must_out_count, turn_score, is_5_must_A,
					is_first_out, color, first_type);
		}

	}

	public int Player_Can_out_card(int hand_card_data[], int cbHandCardCount, int cbOutCardData[], int out_card_count,
			int card_data[], int turn_card_count, int turn_card_data[], int must_out_data[], int must_out_count[],
			int turn_score, boolean is_5_must_A, boolean is_first_out) {
		int card_out_count = 0;
		int can_out_card_data_temp[] = new int[cbHandCardCount];
		int can_out_card_count_temp = 0;
		// 首出
		if (out_card_count == 0) {
			// 無主
			if (this._zhu_type == 4) {
				if (this.has_rule(GameConstants.GAME_RULE_XPSJ_NO_ZHU_DA_CHANG_ZHU) && is_first_out) {
					for (int i = 0; i < cbHandCardCount; i++) {
						if (GetCardValue(hand_card_data[i]) == _chang_zhu_three
								|| GetCardValue(cbOutCardData[i]) == _chang_zhu_four
								|| GetCardValue(cbOutCardData[i]) == _chang_zhu_one
								|| GetCardValue(cbOutCardData[i]) == _chang_zhu_two) {
							can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
						}
					}
				}

			}
			if (can_out_card_count_temp == 0) {
				for (int i = 0; i < cbHandCardCount; i++) {
					can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
				}
			}
			if (turn_card_count > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 判断分牌能不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5
							|| this.GetCardValue(can_out_card_data_temp[i]) == 13
							|| this.GetCardValue(can_out_card_data_temp[i]) == 10) {
						int temp[] = new int[turn_card_count];
						temp[0] = can_out_card_data_temp[i];
						if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					} else {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}
				}

				if (card_out_count == 0) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							return card_out_count;
						}
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							return card_out_count;
						}

					}
				}
			} else {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					card_data[card_out_count++] = can_out_card_data_temp[i];
				}
			}

			return card_out_count;
		}

		int first_type = this.GetCardType(cbOutCardData, out_card_count);
		int color = this.GetCardColor(cbOutCardData[0]);
		if (GetCardValue(cbOutCardData[0]) == _chang_zhu_three || GetCardValue(cbOutCardData[0]) == _chang_zhu_four
				|| GetCardValue(cbOutCardData[0]) == _chang_zhu_one
				|| GetCardValue(cbOutCardData[0]) == _chang_zhu_two) {
			color = this._zhu_type;
		}

		if (GetCardColor_Count(hand_card_data, cbHandCardCount, color) >= out_card_count) {
			for (int i = 0; i < cbHandCardCount; i++) {
				if (color == _zhu_type) {
					if (hand_card_data[i] > this._zhu_value) {
						can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];

					}
				} else {
					if (this.GetCardColor(hand_card_data[i]) == color && (hand_card_data[i] < this._zhu_value)) {
						can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
					}
				}
			}
		} else {//主牌不够的情况
			for (int i = 0; i < cbHandCardCount; i++) {
				can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
			}
			if (first_type == SJConstants.XP_SJ_CT_SINGLE) {
				if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						int temp[] = new int[turn_card_count];
						temp[0] = can_out_card_data_temp[i];
						if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count > 0) {
						return card_out_count;
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 判断分牌能不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							int temp[] = new int[turn_card_count];
							temp[0] = can_out_card_data_temp[i];
							if (!this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
					}
				} else {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 判断分牌能不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5
								|| this.GetCardValue(can_out_card_data_temp[i]) == 13
								|| this.GetCardValue(can_out_card_data_temp[i]) == 10
								|| this.GetCardValue(can_out_card_data_temp[i]) == 14
								|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
							int temp[] = new int[turn_card_count];
							temp[0] = can_out_card_data_temp[i];
							if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						} else {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}

				if (card_out_count == 0) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){
						//10和k
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						//小王
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						//大王
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
					}else{
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
					}
					
				}
			} else if (first_type == SJConstants.XP_SJ_CT_DOUBLE) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (color == _zhu_type) {
						if (this.GetCardColor(can_out_card_data_temp[i]) == color
								|| this.GetCardValue(hand_card_data[i]) == _chang_zhu_one
								|| GetCardValue(can_out_card_data_temp[i]) == _chang_zhu_two
								|| this.GetCardValue(can_out_card_data_temp[i]) == _chang_zhu_three
								|| GetCardValue(can_out_card_data_temp[i]) == _chang_zhu_four) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					} else {
						if (this.GetCardColor(can_out_card_data_temp[i]) == color
								&& GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_one
								&& GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_two
								&& this.GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_three
								&& GetCardValue(can_out_card_data_temp[i]) != _chang_zhu_four) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				if (card_out_count > 0) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						//小王
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						//大王
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						
						return card_out_count;
					}else{//240分玩法
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
						return card_out_count;
					}

				} else {
					tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
					this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);
					if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
						for (int i = 0; i < can_out_card_count_temp; i++) {
							if (can_out_card_data_temp[i] == 0) {
								continue;
							}
							int card_color = this.GetCardColor(can_out_card_data_temp[i]);
							int index = this.get_card_index(can_out_card_data_temp[i]);

							// 先不取分牌
							if (IndexResult.card_index[card_color][index] >= 2) {
								if (this.comparecarddata(turn_card_data, turn_card_count,
										IndexResult.card_data[card_color][index], 2)) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
									can_out_card_data_temp[i] = 0;
								}
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
					} else {
						for (int i = 0; i < can_out_card_count_temp; i++) {
							if (can_out_card_data_temp[i] == 0) {
								continue;
							}
							int card_color = this.GetCardColor(can_out_card_data_temp[i]);
							int index = this.get_card_index(can_out_card_data_temp[i]);

							// 先不取分牌
							if (IndexResult.card_index[card_color][index] >= 2) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 5
										|| this.GetCardValue(can_out_card_data_temp[i]) == 13
										|| this.GetCardValue(can_out_card_data_temp[i]) == 10
										|| this.GetCardValue(can_out_card_data_temp[i]) == 14
										|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
									if (this.comparecarddata(turn_card_data, turn_card_count,
											IndexResult.card_data[card_color][index], 2)) {
										card_data[card_out_count++] = can_out_card_data_temp[i];
										can_out_card_data_temp[i] = 0;
									}
								}
							}
						}
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}

					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					if (card_out_count < out_card_count) {
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 只有分牌的情况，捡小的出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
							for (int i = 0; i < can_out_card_count_temp; i++) {
								// 只有分牌的情况，捡小的出
								if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
							//小王
							for (int i = 0; i < can_out_card_count_temp; i++) {
								// 只有分牌的情况，捡小的出
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
							//大王
							for (int i = 0; i < can_out_card_count_temp; i++) {
								// 只有分牌的情况，捡小的出
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
						}else{
							for (int i = 0; i < can_out_card_count_temp; i++) {
								// 只有分牌的情况，捡小的出
								if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
						}

					}
					return card_out_count;
				}
			} else {//拖拉机
				int link_count = out_card_count / 2;
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (color == _zhu_type) {
						if (this.GetCardColor(can_out_card_data_temp[i]) == color
								|| can_out_card_data_temp[i] > this._zhu_value) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					} else {
						if (this.GetCardColor(can_out_card_data_temp[i]) == color
								&& can_out_card_data_temp[i] < this._zhu_value) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				if (card_out_count > 0 || color == _zhu_type) {
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 分牌不能出
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];

						}
					}
					if (card_out_count < out_card_count) {
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) != 5
									&& this.GetCardValue(can_out_card_data_temp[i]) != 13
									&& this.GetCardValue(can_out_card_data_temp[i]) != 10
									&& this.GetCardValue(can_out_card_data_temp[i]) != 14
									&& this.GetCardValue(can_out_card_data_temp[i]) != 15
									&& can_out_card_data_temp[i] != 0) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}

						int other_must_out_data[] = new int[can_out_card_count_temp];
						int other_must_out_count = 0;
						for (int i = 0; i < can_out_card_count_temp; i++) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								other_must_out_data[other_must_out_count++] = can_out_card_data_temp[i];
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						for (int i = 0; i < other_must_out_count; i++) {
							must_out_data[must_out_count[0]++] = other_must_out_data[i];
						}
						if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
							// 10和k
							for (int i = 0; i < can_out_card_count_temp; i++) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
							//小王
							for (int i = 0; i < can_out_card_count_temp; i++) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								
							}
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
							//大王
							for (int i = 0; i < can_out_card_count_temp; i++) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								
							}
							//if (card_out_count >= out_card_count) {
							//	return card_out_count;
							//}
						}else{
							// 都是分牌
							for (int i = 0; i < can_out_card_count_temp; i++) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
						}
					}
				} else {
					int can_double_count = 0;
					boolean is_can_guan = false;
					tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
					this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);

					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 先不取分牌
						if (IndexResult.card_index[card_color][index] >= 2
								&& can_out_card_data_temp[i] > this._zhu_value) {
							boolean is_link = true;
							for (int next_index = index; next_index > index - link_count; next_index--) {
								if (IndexResult.card_index[card_color][next_index] < 2 || 4 == this._zhu_type) {
									is_link = false;
									break;
								}
							}
							if (is_link) {
								int compare_data[] = new int[cbHandCardCount];
								int compare_count = 0;
								for (int next_index = index; next_index > index - link_count; next_index--) {
									for (int j = 0; j < IndexResult.card_index[card_color][next_index]; j++) {
										compare_data[compare_count++] = IndexResult.card_data[card_color][next_index][j];
									}
								}
								if (this.comparecarddata(turn_card_data, turn_card_count, compare_data,
										out_card_count)) {
									for (int j = 0; j < compare_count; j++) {
										card_data[card_out_count++] = compare_data[j];
									}
									for (int next_index = index; next_index > index - link_count; next_index--) {
										IndexResult.card_index[card_color][next_index] = 0;
									}
									is_can_guan = true;
									continue;
								}
							}
						}
					}

					if (is_can_guan && this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN)
							&& turn_score > 0) {
						return card_out_count;
					}

					int no_score_count = 0;
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] > 0) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) != 5
									&& this.GetCardValue(can_out_card_data_temp[i]) != 13
									&& this.GetCardValue(can_out_card_data_temp[i]) != 10
									&& this.GetCardValue(can_out_card_data_temp[i]) != 14
									&& this.GetCardValue(can_out_card_data_temp[i]) != 15
									&& can_out_card_data_temp[i] != 0) {
								no_score_count++;
							}
						}

					}
					if (no_score_count < out_card_count && card_out_count >= out_card_count) {
						return card_out_count;
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] > 0) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) != 5
									&& this.GetCardValue(can_out_card_data_temp[i]) != 13
									&& this.GetCardValue(can_out_card_data_temp[i]) != 10
									&& this.GetCardValue(can_out_card_data_temp[i]) != 14
									&& this.GetCardValue(can_out_card_data_temp[i]) != 15
									&& can_out_card_data_temp[i] != 0) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}

					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] > 0) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) != 5
									&& this.GetCardValue(can_out_card_data_temp[i]) != 13
									&& this.GetCardValue(can_out_card_data_temp[i]) != 10
									&& this.GetCardValue(can_out_card_data_temp[i]) != 14
									&& this.GetCardValue(can_out_card_data_temp[i]) != 15
									&& can_out_card_data_temp[i] != 0) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}

					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] > 0) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}

					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					for (int i = 0; i < can_out_card_count_temp; i++) {
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] > 0) {
							// 分牌不能出
							if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
								must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							}
						}

					}
					if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
						//都是分
						for (int i = 0; i < can_out_card_count_temp; i++) {
							int card_color = this.GetCardColor(can_out_card_data_temp[i]);
							int index = this.get_card_index(can_out_card_data_temp[i]);
							if (IndexResult.card_index[card_color][index] > 0) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						//小王
						for (int i = 0; i < can_out_card_count_temp; i++) {
							int card_color = this.GetCardColor(can_out_card_data_temp[i]);
							int index = this.get_card_index(can_out_card_data_temp[i]);
							if (IndexResult.card_index[card_color][index] > 0) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
						}
						if (card_out_count >= out_card_count) {
							return card_out_count;
						}
						
						//大王
						for (int i = 0; i < can_out_card_count_temp; i++) {
							int card_color = this.GetCardColor(can_out_card_data_temp[i]);
							int index = this.get_card_index(can_out_card_data_temp[i]);
							if (IndexResult.card_index[card_color][index] > 0) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}
						}
					}else{
						for (int i = 0; i < can_out_card_count_temp; i++) {
							int card_color = this.GetCardColor(can_out_card_data_temp[i]);
							int index = this.get_card_index(can_out_card_data_temp[i]);
							if (IndexResult.card_index[card_color][index] > 0) {
								if (this.GetCardValue(can_out_card_data_temp[i]) == 10 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 13 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 14 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
								if (this.GetCardValue(can_out_card_data_temp[i]) == 15 && can_out_card_data_temp[i] != 0) {
									card_data[card_out_count++] = can_out_card_data_temp[i];
								}
							}

						}
					}
					
				}
			}

			return card_out_count;
		}

		//主牌够的情况
		if (first_type == SJConstants.XP_SJ_CT_SINGLE) {

			if (is_5_must_A) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 判断分牌能不能出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 1) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						return card_out_count;
					}
				}
			}
			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					int temp[] = new int[turn_card_count];
					temp[0] = can_out_card_data_temp[i];
					if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				}
				if (card_out_count > 0) {
					return card_out_count;
				}
			}
			for (int i = 0; i < can_out_card_count_temp; i++) {
				// 判断分牌能不能出
				if (this.GetCardValue(can_out_card_data_temp[i]) == 5
						|| this.GetCardValue(can_out_card_data_temp[i]) == 13
						|| this.GetCardValue(can_out_card_data_temp[i]) == 10
						|| this.GetCardValue(can_out_card_data_temp[i]) == 14
						|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
					int temp[] = new int[turn_card_count];
					temp[0] = can_out_card_data_temp[i];
					if (this.comparecarddata(turn_card_data, turn_card_count, temp, 1)) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				} else {
					card_data[card_out_count++] = can_out_card_data_temp[i];
					can_out_card_data_temp[i] = 0;
				}
			}

			if (card_out_count == 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					// 只有分牌的情况，捡小的出
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5 && can_out_card_data_temp[i] != 0) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						return card_out_count;
					}
				}
				//300分玩法
				if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if ((GetCardValue(can_out_card_data_temp[i]) == 13|| GetCardValue(can_out_card_data_temp[i]) == 10)){
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
					if(card_out_count > 0){
						return card_out_count;
					}
					
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if ((GetCardValue(can_out_card_data_temp[i]) == 14)){
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
					if(card_out_count > 0){
						return card_out_count;
					}
					
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if ((GetCardValue(can_out_card_data_temp[i]) == 15)){
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}	
					if(card_out_count > 0){
						return card_out_count;
					}
				}else{//240分玩法
					for (int i = 0; i < can_out_card_count_temp; i++) {
						// 只有分牌的情况，捡小的出
						if ((this.GetCardValue(can_out_card_data_temp[i]) == 13
								|| this.GetCardValue(can_out_card_data_temp[i]) == 10
								|| this.GetCardValue(can_out_card_data_temp[i]) == 14
								|| this.GetCardValue(can_out_card_data_temp[i]) == 15) && can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
					if (card_out_count > 0) {
						return card_out_count;
					}
				}

			}
		} else if (first_type == SJConstants.XP_SJ_CT_DOUBLE) {
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);
			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.comparecarddata(turn_card_data, turn_card_count,
								IndexResult.card_data[card_color][index], 2)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}

				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
			}
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);

				// 先不取分牌
				if (IndexResult.card_index[card_color][index] >= 2) {
					if (this.GetCardValue(can_out_card_data_temp[i]) == 5
							|| this.GetCardValue(can_out_card_data_temp[i]) == 13
							|| this.GetCardValue(can_out_card_data_temp[i]) == 10
							|| this.GetCardValue(can_out_card_data_temp[i]) == 14) {
						if (this.comparecarddata(turn_card_data, turn_card_count,
								IndexResult.card_data[card_color][index], 2)) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					} else {
						card_data[card_out_count++] = can_out_card_data_temp[i];
						can_out_card_data_temp[i] = 0;
					}
				}
			}
			if (card_out_count >= out_card_count) {
				return card_out_count;
			}
			if (card_out_count < out_card_count) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 只有分牌的情况，捡小的出
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
					//先添加10和k
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					//再添加小王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					//再添加大王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
				}else{//240分玩法
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								can_out_card_data_temp[i] = 0;
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
				}

				for (int i = 0; i < card_out_count; i++) {
					must_out_data[must_out_count[0]++] = card_data[i];
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先不要分牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先不要分牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15
								&& can_out_card_data_temp[i] != 0) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
						}
					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 先5
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							can_out_card_data_temp[i] = 0;
							if (card_out_count == out_card_count) {
								return card_out_count;
							}
						}
					}
				}
				if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 单牌 再10 k
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10 || 
									this.GetCardValue(can_out_card_data_temp[i]) == 13){
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					// 小王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14){
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					// 大王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15){
								card_data[card_out_count++] = can_out_card_data_temp[i];
							}
						}
					}
				}else{
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 单牌 再10 k 大小王
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10
									|| this.GetCardValue(can_out_card_data_temp[i]) == 13
									|| this.GetCardValue(can_out_card_data_temp[i]) == 14
									|| this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];

							}
						}
					}
				}
			}
		} else {//拖拉机
			int link_count = out_card_count / 2;
			int can_double_count = 0;
			int out_max_index = this.get_card_index(turn_card_data[0]);
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(can_out_card_data_temp, can_out_card_count_temp, IndexResult);
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);
				if (IndexResult.card_index[card_color][index] >= 2) {
					can_double_count++;
					i++;
				}

			}
			if (can_double_count >= link_count) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 先不取分牌
					if (IndexResult.card_index[card_color][index] >= 2) {
						boolean is_link = true;
						for (int next_index = index; next_index > index - link_count; next_index--) {
							if (IndexResult.card_index[card_color][next_index] < 2 && index - next_index < link_count) {
								is_link = false;
								break;
							}
						}
						if (is_link && index > out_max_index) {
							int compare_data[] = new int[out_card_count];
							int compare_count = 0;
							for (int next_index = index; next_index > index - link_count; next_index--) {
								for (int j = 0; j < IndexResult.card_index[card_color][next_index]; j++) {
									compare_data[compare_count++] = IndexResult.card_data[card_color][next_index][j];
								}
							}
							if (this.comparecarddata(turn_card_data, turn_card_count, compare_data, out_card_count)) {
								for (int j = 0; j < compare_count; j++) {
									card_data[card_out_count++] = compare_data[j];
								}
								for (int next_index = index; next_index > index - link_count; next_index--) {
									IndexResult.card_index[card_color][next_index] = 0;
								}
								continue;
							}
						}
					}
				}
			}

			if (this.has_rule(GameConstants.GAME_RULE_XPSJ_HAVE_SCORE_BI_GUAN) && turn_score > 0) {
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
			}

			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);
				// 先不取分牌
				if (IndexResult.card_index[card_color][index] >= 2) {
					if (this.GetCardValue(can_out_card_data_temp[i]) != 5
							&& this.GetCardValue(can_out_card_data_temp[i]) != 13
							&& this.GetCardValue(can_out_card_data_temp[i]) != 10
							&& this.GetCardValue(can_out_card_data_temp[i]) != 14
							&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
						card_data[card_out_count++] = can_out_card_data_temp[i];
					}

				}
			}
			if (card_out_count < out_card_count) {
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 先不取分牌
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}

					}
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 只有分牌的情况，捡小的出
					if (IndexResult.card_index[card_color][index] >= 2) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							if (card_out_count >= out_card_count) {
								return card_out_count;
							}
						}
					}
				}
				if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
					//先添加10和k
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					//添加小王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					//添大王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					
				}else{
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						if (IndexResult.card_index[card_color][index] >= 2) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (can_double_count < link_count) {
									must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
								}
							}

						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
				}
	
				//单牌
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
						}
					}
				}
				if (card_out_count >= out_card_count) {
					return card_out_count;
				}
				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) != 5
								&& this.GetCardValue(can_out_card_data_temp[i]) != 13
								&& this.GetCardValue(can_out_card_data_temp[i]) != 10
								&& this.GetCardValue(can_out_card_data_temp[i]) != 14
								&& this.GetCardValue(can_out_card_data_temp[i]) != 15) {
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
						}
					}
				}

				for (int i = 0; i < can_out_card_count_temp; i++) {
					if (can_out_card_data_temp[i] == 0) {
						continue;
					}
					int card_color = this.GetCardColor(can_out_card_data_temp[i]);
					int index = this.get_card_index(can_out_card_data_temp[i]);
					// 单牌 5
					if (IndexResult.card_index[card_color][index] == 1) {
						if (this.GetCardValue(can_out_card_data_temp[i]) == 5) {
							card_data[card_out_count++] = can_out_card_data_temp[i];
							must_out_data[must_out_count[0]++] = can_out_card_data_temp[i];
							if (card_out_count == out_card_count) {
								return card_out_count;
							}
						}
					}
				}
				if(this.has_rule(GameConstants.GAME_RULE_YZSJ_300)){//300分玩法
					//先添加10和k
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 单牌 10 k 小王和大王
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					//小王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 小王和大王
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
					
					//大王
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 大王
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
				}else{
					for (int i = 0; i < can_out_card_count_temp; i++) {
						if (can_out_card_data_temp[i] == 0) {
							continue;
						}
						int card_color = this.GetCardColor(can_out_card_data_temp[i]);
						int index = this.get_card_index(can_out_card_data_temp[i]);
						// 单牌 10 k 小王和大王
						if (IndexResult.card_index[card_color][index] == 1) {
							if (this.GetCardValue(can_out_card_data_temp[i]) == 10) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 13) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 14) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
							if (this.GetCardValue(can_out_card_data_temp[i]) == 15) {
								card_data[card_out_count++] = can_out_card_data_temp[i];
								if (card_out_count == out_card_count) {
								}
							}
						}
					}
					if (card_out_count >= out_card_count) {
						return card_out_count;
					}
				}
			}
		}

		return card_out_count;
	}

	public boolean can_down(int first_card[], int first_count, int turn_card_data[], int turn_card_count,
			int out_card[], int out_count, int hand_card_data[], int hand_card_count, int turn_score,
			boolean is_5_must_A, boolean is_first_out) {

		int first_color = this.GetCardColor(first_card[0]);
		if (GetCardValue(first_card[0]) == _chang_zhu_one || GetCardValue(first_card[0]) == _chang_zhu_two
				|| GetCardValue(first_card[0]) == _chang_zhu_three || GetCardValue(first_card[0]) == _chang_zhu_four) {
			first_color = this._zhu_type;
		}
		int first_type = this.GetCardType(first_card, first_count);

		int can_out_data[] = new int[hand_card_count];
		int must_out_data[] = new int[hand_card_count];
		int must_out_count[] = new int[1];
		int can_out_count = player_can_out_card_yz(hand_card_data, hand_card_count, first_card, first_count,
				can_out_data, turn_card_count, turn_card_data, must_out_data, must_out_count, turn_score, is_5_must_A,
				is_first_out);

		if (first_type == SJConstants.XP_SJ_CT_SINGLE) {
			for (int i = 0; i < out_count; i++) {
				for (int j = 0; j < can_out_count; j++) {
					if (out_card[i] == can_out_data[j]) {
						can_out_data[j] = 0;
						break;
					}
					if (j == can_out_count - 1) {
						return false;
					}
				}
			}
		} else {
			int hand_color_count = GetCardColor_Count(hand_card_data, hand_card_count, first_color);

			tagAnalyseCardType out_type_card = new tagAnalyseCardType();
			tagAnalyseCardType can_out_type_card = new tagAnalyseCardType();
			this.get_card_all_type(out_card, out_count, out_type_card, first_type);
			this.get_card_all_type(can_out_data, can_out_count, can_out_type_card, first_type);

			// 出的牌數量大于手上相同顏色的牌數量
			if (turn_card_count > hand_color_count) {
				for (int i = 0; i < out_type_card.type_count; i++) {
					if (out_type_card.card_data[i][0] == 5 || out_type_card.card_data[i][0] == 10
							|| out_type_card.card_data[i][0] == 13 || out_type_card.card_data[i][0] == 14
							|| out_type_card.card_data[i][0] == 15) {
						for (int j = 0; j < can_out_type_card.type_count; j++) {

							if (this.remove_cards_by_data(out_type_card.card_data[i], out_type_card.count[i],
									can_out_type_card.card_data[j], can_out_type_card.count[j])) {
								can_out_type_card.count[j] = 0;
								break;
							}

							if (j == can_out_type_card.type_count - 1) {
								return false;
							}
						}
					}

				}
				for (int i = 0; i < out_count; i++) {
					for (int j = 0; j < can_out_count; j++) {

						if (can_out_data[i] == can_out_data[j]) {
							can_out_data[j] = 0;
							break;
						}
						if (j == can_out_count - 1) {
							return false;
						}
					}
				}

			} else {
				for (int i = 0; i < out_type_card.type_count; i++) {
					for (int j = 0; j < can_out_type_card.type_count; j++) {
						if (this.remove_cards_by_data(out_type_card.card_data[i], out_type_card.count[i],
								can_out_type_card.card_data[j], can_out_type_card.count[j])) {
							can_out_type_card.count[j] = 0;
							break;
						}

						if (j == can_out_type_card.type_count - 1) {
							return false;
						}
					}
				}

			}

		}
		int hand_color_count = GetCardColor_Count(hand_card_data, hand_card_count, first_color);
		if (first_type == SJConstants.XP_SJ_CT_DOUBLE_LINK) {
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			AnalysebCardDataToIndex(hand_card_data, hand_card_count, IndexResult);
			int double_zhu_count = 0;
			int link_count = first_count / 2;
			int can_out_card_data_temp[] = new int[hand_card_count];
			int can_out_card_count_temp = 0;
			int color = GetCardColor(first_card[0]);
			if (first_card[0] > _zhu_value) {
				color = this._zhu_type;
			}
			for (int i = 0; i < hand_card_count; i++) {
				if (color == _zhu_type) {
					if (hand_card_data[i] > _zhu_value) {
						can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];

					}
				} else {
					if (this.GetCardColor(hand_card_data[i]) == color && hand_card_data[i] < _zhu_value) {
						can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
					}
				}
			}
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);
				if (card_color == _zhu_type && IndexResult.card_index[card_color][index] >= 2) {
					double_zhu_count++;
					i++;
				}
			}

			if (double_zhu_count < link_count && double_zhu_count > 0) {
				int must_count = must_out_count[0];
				int must_card[] = must_out_data;
				int out_card_temp[] = new int[out_count];
				for (int i = 0; i < out_count; i++) {
					out_card_temp[i] = out_card[i];
				}
				for (int j = 0; j < must_count; j++) {
					for (int i = 0; i < out_count; i++) {
						if (out_card_temp[i] == must_card[j]) {
							must_card[j] = 0;
							break;
						}
						if (i == out_count - 1) {
							return false;
						}
					}
				}
				must_out_count[0] = 0;
			}
		}

		if (turn_card_count > hand_color_count || must_out_count[0] > 0) {
			int must_count = must_out_count[0];
			int must_card[] = must_out_data;

			boolean is_double = false;
			boolean is_single = false;
			tagAnalyseCardType out_type_card = new tagAnalyseCardType();
			tagAnalyseCardType must_out_type_card = new tagAnalyseCardType();
			this.Analyse_card_type(out_card, out_count, out_type_card);
			this.Analyse_card_type(must_card, must_count, must_out_type_card);
			if (first_type == SJConstants.XP_SJ_CT_DOUBLE || first_type == SJConstants.XP_SJ_CT_DOUBLE_LINK) {
				for (int j = 0; j < must_out_type_card.type_count; j++) {
					if (must_out_type_card.type[j] == SJConstants.XP_SJ_CT_SINGLE) {
						is_single = true;
					}
					if (must_out_type_card.type[j] == SJConstants.XP_SJ_CT_DOUBLE) {
						is_double = true;
					}
				}
			}

			if (is_double && is_double) {
				for (int j = 0; j < must_out_type_card.type_count; j++) {
					for (int i = 0; i < out_type_card.type_count; i++) {
						if (this.remove_cards_by_data(out_type_card.card_data[i], out_type_card.count[i],
								must_out_type_card.card_data[j], must_out_type_card.count[j])) {
							out_type_card.count[i] = 0;
							break;
						}
						if (i == out_type_card.type_count - 1) {
							return false;
						}
					}
				}
			} else {
				int out_card_temp[] = new int[out_count];
				for (int i = 0; i < out_count; i++) {
					out_card_temp[i] = out_card[i];
				}
				for (int j = 0; j < must_count; j++) {
					for (int i = 0; i < out_count; i++) {
						if (out_card_temp[i] == must_card[j]) {
							must_card[j] = 0;
							break;
						}
						if (i == out_count - 1) {
							return false;
						}
					}
				}
			}

		}
		return true;
	}

	public boolean is_he_li(int first_card[], int first_count, int turn_card_data[], int turn_card_count,
			int out_card[], int out_count, int hand_card_data[], int hand_card_count, int turn_score,
			boolean is_5_must_A, boolean is_first_out) {

		int first_color = this.GetCardColor(first_card[0]);
		if (GetCardValue(first_card[0]) == _chang_zhu_one || GetCardValue(first_card[0]) == _chang_zhu_two
				|| GetCardValue(first_card[0]) == _chang_zhu_three || GetCardValue(first_card[0]) == _chang_zhu_four) {
			first_color = this._zhu_type;
		}
		int first_type = this.GetCardType(first_card, first_count);

		int can_out_data[] = new int[hand_card_count];
		int must_out_data[] = new int[hand_card_count];
		int must_out_count[] = new int[1];
		int can_out_count = Player_Can_out_card(hand_card_data, hand_card_count, first_card, first_count, can_out_data,
				turn_card_count, turn_card_data, must_out_data, must_out_count, turn_score, is_5_must_A, is_first_out);

		if (first_type == SJConstants.XP_SJ_CT_SINGLE) {
			for (int i = 0; i < out_count; i++) {
				for (int j = 0; j < can_out_count; j++) {
					if (out_card[i] == can_out_data[j]) {
						can_out_data[j] = 0;
						break;
					}
					if (j == can_out_count - 1) {
						return false;
					}
				}
			}
		} else {
			int hand_color_count = GetCardColor_Count(hand_card_data, hand_card_count, first_color);

			tagAnalyseCardType out_type_card = new tagAnalyseCardType();
			tagAnalyseCardType can_out_type_card = new tagAnalyseCardType();
			this.get_card_all_type(out_card, out_count, out_type_card, first_type);
			this.get_card_all_type(can_out_data, can_out_count, can_out_type_card, first_type);

			if (turn_card_count > hand_color_count) {
				for (int i = 0; i < out_type_card.type_count; i++) {
					if (out_type_card.card_data[i][0] == 5 || out_type_card.card_data[i][0] == 10
							|| out_type_card.card_data[i][0] == 13 || out_type_card.card_data[i][0] == 14
							|| out_type_card.card_data[i][0] == 15) {
						for (int j = 0; j < can_out_type_card.type_count; j++) {

							if (this.remove_cards_by_data(out_type_card.card_data[i], out_type_card.count[i],
									can_out_type_card.card_data[j], can_out_type_card.count[j])) {
								can_out_type_card.count[j] = 0;
								break;
							}

							if (j == can_out_type_card.type_count - 1) {
								return false;
							}
						}
					}

				}
				for (int i = 0; i < out_count; i++) {
					for (int j = 0; j < can_out_count; j++) {

						if (can_out_data[i] == can_out_data[j]) {
							can_out_data[j] = 0;
							break;
						}
						if (j == can_out_count - 1) {
							return false;
						}
					}
				}

			} else {
				for (int i = 0; i < out_type_card.type_count; i++) {
					for (int j = 0; j < can_out_type_card.type_count; j++) {
						if (this.remove_cards_by_data(out_type_card.card_data[i], out_type_card.count[i],
								can_out_type_card.card_data[j], can_out_type_card.count[j])) {
							can_out_type_card.count[j] = 0;
							break;
						}

						if (j == can_out_type_card.type_count - 1) {
							return false;
						}
					}
				}

			}

		}
		int hand_color_count = GetCardColor_Count(hand_card_data, hand_card_count, first_color);
		if (first_type == SJConstants.XP_SJ_CT_DOUBLE_LINK) {
			tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
			this.AnalysebCardDataToIndex(hand_card_data, hand_card_count, IndexResult);
			int double_zhu_count = 0;
			int link_count = first_count / 2;
			int can_out_card_data_temp[] = new int[hand_card_count];
			int can_out_card_count_temp = 0;
			int color = this.GetCardColor(first_card[0]);
			if (first_card[0] > this._zhu_value) {
				color = this._zhu_type;
			}
			for (int i = 0; i < hand_card_count; i++) {
				if (color == _zhu_type) {
					if (hand_card_data[i] > this._zhu_value) {
						can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];

					}
				} else {
					if (this.GetCardColor(hand_card_data[i]) == color && hand_card_data[i] < _zhu_value) {
						can_out_card_data_temp[can_out_card_count_temp++] = hand_card_data[i];
					}
				}
			}
			for (int i = 0; i < can_out_card_count_temp; i++) {
				if (can_out_card_data_temp[i] == 0) {
					continue;
				}
				int card_color = this.GetCardColor(can_out_card_data_temp[i]);
				int index = this.get_card_index(can_out_card_data_temp[i]);
				if (card_color == _zhu_type && IndexResult.card_index[card_color][index] >= 2) {
					double_zhu_count++;
					i++;
				}
			}

			if (double_zhu_count < link_count && double_zhu_count > 0) {
				int must_count = must_out_count[0];
				int must_card[] = must_out_data;
				int out_card_temp[] = new int[out_count];
				for (int i = 0; i < out_count; i++) {
					out_card_temp[i] = out_card[i];
				}
				for (int j = 0; j < must_count; j++) {
					for (int i = 0; i < out_count; i++) {
						if (out_card_temp[i] == must_card[j]) {
							must_card[j] = 0;
							break;
						}
						if (i == out_count - 1) {
							return false;
						}
					}
				}
				must_out_count[0] = 0;
			}
		}

		if (turn_card_count > hand_color_count || must_out_count[0] > 0) {
			int must_count = must_out_count[0];
			int must_card[] = must_out_data;

			boolean is_double = false;
			boolean is_single = false;
			tagAnalyseCardType out_type_card = new tagAnalyseCardType();
			tagAnalyseCardType must_out_type_card = new tagAnalyseCardType();
			this.Analyse_card_type(out_card, out_count, out_type_card);
			this.Analyse_card_type(must_card, must_count, must_out_type_card);
			if (first_type == SJConstants.XP_SJ_CT_DOUBLE || first_type == SJConstants.XP_SJ_CT_DOUBLE_LINK) {
				for (int j = 0; j < must_out_type_card.type_count; j++) {
					if (must_out_type_card.type[j] == SJConstants.XP_SJ_CT_SINGLE) {
						is_single = true;
					}
					if (must_out_type_card.type[j] == SJConstants.XP_SJ_CT_DOUBLE) {
						is_double = true;
					}
				}
			}

			if (is_double && is_double) {
				for (int j = 0; j < must_out_type_card.type_count; j++) {
					for (int i = 0; i < out_type_card.type_count; i++) {
						if (this.remove_cards_by_data(out_type_card.card_data[i], out_type_card.count[i],
								must_out_type_card.card_data[j], must_out_type_card.count[j])) {
							out_type_card.count[i] = 0;
							break;
						}
						if (i == out_type_card.type_count - 1) {
							return false;
						}
					}
				}
			} else {
				int out_card_temp[] = new int[out_count];
				for (int i = 0; i < out_count; i++) {
					out_card_temp[i] = out_card[i];
				}
				for (int j = 0; j < must_count; j++) {
					for (int i = 0; i < out_count; i++) {
						if (out_card_temp[i] == must_card[j]) {
							must_card[j] = 0;
							break;
						}
						if (i == out_count - 1) {
							return false;
						}
					}
				}
			}

		}
		return true;
	}

	public boolean comparecarddata_yz(int first_card[], int first_count, int next_card[], int next_count) {
		if (first_count == 0) {
			return true;
		}
		tagAnalyseIndexResult_Xpsj IndexResult_first = new tagAnalyseIndexResult_Xpsj();
		tagAnalyseIndexResult_Xpsj IndexResult_next = new tagAnalyseIndexResult_Xpsj();
		AnalysebCardDataToIndex(first_card, first_count, IndexResult_first);
		AnalysebCardDataToIndex(next_card, next_count, IndexResult_next);

		int first_type = this.GetCardType(first_card, first_count);
		int next_type = this.GetCardType(next_card, next_count);
		int first_color = this.GetCardColor(first_card[first_count - 1]);
		int next_color = this.GetCardColor(next_card[next_count - 1]);

		if (this.GetCardValue(first_card[0]) == _chang_zhu_one || this.GetCardValue(first_card[0]) == _chang_zhu_two
				|| this.GetCardValue(first_card[0]) == _chang_zhu_three
				|| this.GetCardValue(first_card[0]) == _chang_zhu_four) {
			first_color = this._zhu_type;
		}
		if (this.GetCardValue(next_card[0]) == _chang_zhu_one || this.GetCardValue(next_card[0]) == _chang_zhu_two
				|| this.GetCardValue(next_card[0]) == _chang_zhu_three
				|| this.GetCardValue(next_card[0]) == _chang_zhu_four) {
			next_color = this._zhu_type;
		}

		if (first_color == next_color) {
			if (next_type == first_type) {
				int frist_index = get_card_index_yz(first_card[first_count - 1]);
				int next_index = get_card_index_yz(next_card[next_count - 1]);

				if (first_type == SJConstants.XP_SJ_CT_DOUBLE_LINK) {
					for (int i = 0; i < first_count; i++) {
						if (frist_index < get_card_index_yz(first_card[i])) {
							frist_index = get_card_index_yz(first_card[i]);
						}
						if (next_index < get_card_index_yz(next_card[i])) {
							next_index = get_card_index_yz(next_card[i]);
						}
					}
					return next_index > frist_index;

				} else if (first_type == SJConstants.XP_SJ_CT_ERROR) {
					if (first_color == _zhu_type && next_color == _zhu_type) {
						tagAnalyseCardType first_type_card = new tagAnalyseCardType();
						tagAnalyseCardType next_type_card = new tagAnalyseCardType();
						Analyse_card_type(first_card, first_count, first_type_card);
						Analyse_card_type(next_card, next_count, next_type_card);
						for (int i = 0; i < next_type_card.type_count; i++) {
							if (next_type_card.type[i] != SJConstants.XP_SJ_CT_DOUBLE) {
								return false;
							}
						}
						int first_value = GetCardLogicValue(first_type_card.card_data[0][0]);
						int first_color_temp = this.GetCardColor(first_type_card.card_data[0][0]);
						for (int i = 0; i < next_type_card.type_count; i++) {
							if (GetCardLogicValue(next_type_card.card_data[i][0]) > first_value) {
								return true;
							} else if (GetCardLogicValue(next_type_card.card_data[i][0]) == first_value) {
								if (first_color_temp == this._zhu_type) {
									return false;
								} else if (GetCardColor(next_type_card.card_data[i][0]) == this._zhu_type) {
									return true;
								} else {
									first_value = GetCardLogicValue(first_type_card.card_data[1][0]);
									for (int j = 0; j < next_type_card.count[i]; j++) {
										next_type_card.card_data[i][j] = 0;
									}
									i = 0;
								}

							}
						}
						return true;
					} else {
						return false;
					}
				} else {
					/*
					 * if (GetCardValue(first_card[0]) == this._chang_zhu_one ||
					 * GetCardValue(first_card[0]) == this._chang_zhu_two) {
					 * frist_index += 4; } if (GetCardValue(next_card[0]) ==
					 * this._chang_zhu_one || GetCardValue(next_card[0]) ==
					 * this._chang_zhu_two) { next_index += 4; } if
					 * (GetCardValue(first_card[0]) == this._chang_zhu_three) {
					 * if (this.GetCardColor(first_card[0]) == _zhu_type) {
					 * frist_index = 15; } else { frist_index = 14; } } if
					 * (GetCardValue(next_card[0]) == this._chang_zhu_three) {
					 * if (this.GetCardColor(next_card[0]) == _zhu_type) {
					 * next_index = 15; } else { next_index = 14; } } if
					 * (GetCardValue(first_card[0]) == this._chang_zhu_four) {
					 * if (this.GetCardColor(first_card[0]) == _zhu_type) {
					 * frist_index = 13; } else { frist_index = 12; } } if
					 * (GetCardValue(next_card[0]) == this._chang_zhu_four) { if
					 * (this.GetCardColor(next_card[0]) == _zhu_type) {
					 * next_index = 13; } else { next_index = 12; } }
					 */
				}

				if (frist_index >= next_index) {
					return false;
				} else {
					return true;
				}
			} else {

				return false;
			}
		} else {
			if (first_color == _zhu_type) {
				return false;
			} else if (next_color == _zhu_type) {
				if (next_type == first_type) {
					return true;
				} else {
					tagAnalyseCardType next_type_card = new tagAnalyseCardType();
					Analyse_card_type(next_card, next_count, next_type_card);
					for (int i = 0; i < next_type_card.type_count; i++) {
						if (next_type_card.type[i] != SJConstants.XP_SJ_CT_DOUBLE) {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
	}

	public boolean comparecarddata(int first_card[], int first_count, int next_card[], int next_count) {
		if (first_count == 0) {
			return true;
		}
		tagAnalyseIndexResult_Xpsj IndexResult_first = new tagAnalyseIndexResult_Xpsj();
		tagAnalyseIndexResult_Xpsj IndexResult_next = new tagAnalyseIndexResult_Xpsj();
		AnalysebCardDataToIndex(first_card, first_count, IndexResult_first);
		AnalysebCardDataToIndex(next_card, next_count, IndexResult_next);

		int first_type = this.GetCardType(first_card, first_count);
		int next_type = this.GetCardType(next_card, next_count);
		int first_color = this.GetCardColor(first_card[first_count - 1]);
		int next_color = this.GetCardColor(next_card[next_count - 1]);

		if (this.GetCardValue(first_card[0]) == _chang_zhu_one 
				|| this.GetCardValue(first_card[0]) == _chang_zhu_two
				|| this.GetCardValue(first_card[0]) == _chang_zhu_three
				|| this.GetCardValue(first_card[0]) == _chang_zhu_four) {
			first_color = this._zhu_type;
		}
		if (this.GetCardValue(next_card[0]) == _chang_zhu_one 
				|| this.GetCardValue(next_card[0]) == _chang_zhu_two
				|| this.GetCardValue(next_card[0]) == _chang_zhu_three
				|| this.GetCardValue(next_card[0]) == _chang_zhu_four) {
			next_color = this._zhu_type;
		}
		if (first_color == next_color) {
			if (next_type == first_type) {
				int frist_index = get_card_index_yz(first_card[first_count - 1]);
				int next_index = get_card_index_yz(next_card[next_count - 1]);

				if (first_type == SJConstants.XP_SJ_CT_DOUBLE_LINK) {
					for (int i = 0; i < first_count; i++) {
						if (frist_index < get_card_index_yz(first_card[i])) {
							frist_index = get_card_index_yz(first_card[i]);
						}
						if (next_index < get_card_index_yz(next_card[i])) {
							next_index = get_card_index_yz(next_card[i]);
						}
					}
					return next_index > frist_index;

				} else if (first_type == SJConstants.XP_SJ_CT_ERROR) {
					if (first_color == _zhu_type && next_color == _zhu_type) {
						tagAnalyseCardType first_type_card = new tagAnalyseCardType();
						tagAnalyseCardType next_type_card = new tagAnalyseCardType();
						Analyse_card_type(first_card, first_count, first_type_card);
						Analyse_card_type(next_card, next_count, next_type_card);
						for (int i = 0; i < next_type_card.type_count; i++) {
							if (next_type_card.type[i] != SJConstants.XP_SJ_CT_DOUBLE) {
								return false;
							}
						}
						int first_value = GetCardLogicValue(first_type_card.card_data[0][0]);
						int first_color_temp = this.GetCardColor(first_type_card.card_data[0][0]);
						for (int i = 0; i < next_type_card.type_count; i++) {
							if (GetCardLogicValue(next_type_card.card_data[i][0]) > first_value) {
								return true;
							} else if (GetCardLogicValue(next_type_card.card_data[i][0]) == first_value) {
								if (first_color_temp == this._zhu_type) {
									return false;
								} else if (GetCardColor(next_type_card.card_data[i][0]) == this._zhu_type) {
									return true;
								} else {
									first_value = GetCardLogicValue(first_type_card.card_data[1][0]);
									for (int j = 0; j < next_type_card.count[i]; j++) {
										next_type_card.card_data[i][j] = 0;
									}
									i = 0;
								}

							}
						}
						return true;
					} else {
						return false;
					}
				} else {
					
					  /*if (GetCardValue(first_card[0]) == this._chang_zhu_one ||GetCardValue(first_card[0]) == this._chang_zhu_two) {
						  frist_index += 4; 
					  } 
					  if (GetCardValue(next_card[0]) ==this._chang_zhu_one || GetCardValue(next_card[0]) ==this._chang_zhu_two) {
						  next_index += 4; 
					  } 
					  if(GetCardValue(first_card[0]) == this._chang_zhu_three) {
						  if (this.GetCardColor(first_card[0]) == _zhu_type) {
							  frist_index = 15; 
						  } else { 
							  frist_index = 14; 
						  } 
					  } 
					  if(GetCardValue(next_card[0]) == this._chang_zhu_three) {
						  if (this.GetCardColor(next_card[0]) == _zhu_type) {
							  next_index = 15; 
						  } else { 
							  next_index = 14; 
						  } 
					  } 
					  if(GetCardValue(first_card[0]) == this._chang_zhu_four) {
						  if (this.GetCardColor(first_card[0]) == _zhu_type) {
							  frist_index = 13; 
						  } else { 
							  frist_index = 12; 
						  } 
					  } 
					  if(GetCardValue(next_card[0]) == this._chang_zhu_four) { 
						  if(this.GetCardColor(next_card[0]) == _zhu_type) {
							  next_index = 13; 
						  } else {
							  next_index = 12; 
						 } 
					} */
					frist_index = get_card_index_yz(first_card[0]);
					next_index = get_card_index_yz(next_card[0]);
				}

				if (frist_index >= next_index) {
					return false;
				} else {
					return true;
				}
			} else {

				return false;
			}
		} else {
			if (first_color == _zhu_type) {
				return false;
			} else if (next_color == _zhu_type) {
				if (next_type == first_type) {
					return true;
				} else {
					tagAnalyseCardType next_type_card = new tagAnalyseCardType();
					Analyse_card_type(next_card, next_count, next_type_card);
					for (int i = 0; i < next_type_card.type_count; i++) {
						if (next_type_card.type[i] != SJConstants.XP_SJ_CT_DOUBLE) {
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
	}

	// 分析牌型
	public void Analyse_card_type(int cbCardData[], int cbCardCount, tagAnalyseCardType type_card) {
		tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
		AnalysebCardDataToIndex(cbCardData, cbCardCount, IndexResult);
		for (int i = SJConstants.XP_SJ_MAX_INDEX - 1; i >= 0; i--) {
			for (int color = 0; color < 5; color++) {
				if (IndexResult.card_index[color][i] == 2) {
					type_card.count[type_card.type_count] = IndexResult.card_index[color][i];
					type_card.type[type_card.type_count] = SJConstants.XP_SJ_CT_DOUBLE;
					for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
						type_card.card_data[type_card.type_count][j] = IndexResult.card_data[color][i][j];
					}
					type_card.type_count++;
				} else {
					for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
						type_card.count[type_card.type_count] = 1;
						type_card.type[type_card.type_count] = SJConstants.XP_SJ_CT_SINGLE;
						type_card.card_data[type_card.type_count][0] = IndexResult.card_data[color][i][j];
						type_card.type_count++;
					}
				}
			}
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	public int GetCardLogicValue(int CardData) {
		// 扑克属性
		int cbCardColor = GetCardColor(CardData);
		int cbCardValue = GetCardValue(CardData);

		// 转换数值
		if (cbCardValue >= 14)
			return cbCardValue + 3;
		if (cbCardValue == 1 || cbCardValue == 2)
			return cbCardValue + 13;
		return cbCardValue;
	}

	public int get_card_index(int card_data) {
		int index = GetCardLogicValue(card_data);
		return index - 3;
	}

	public int get_card_index_yz(int card_data) {
		int index = GetCardLogicValue(card_data);
		index -= 3;
		// 无主
		if (this._zhu_type == 4) {
			int temp = index;
			if (index == 12) {
				int color = this.GetCardColor(card_data);
				temp = 12 + color;
			} else if (index == 7) {
				int color = this.GetCardColor(card_data);
				temp = 17 + color;
			}  else if (index == 14) {
				temp = 24;
			} else if (index == 15) {
				temp = 25;
			}
			index = temp;
		} else {
			int temp = index;
			if (index == 12) {
				int color = this.GetCardColor(card_data);
				if (color != this._zhu_type) {
					temp = 15;
				} else {
					temp = 16;
				}

			} else if (index == 7) {
				int color = this.GetCardColor(card_data);
				if (color != this._zhu_type) {
					temp = 17;
				} else {
					temp = 18;
				}
			} else if (index == 14) {
				temp = 19;
			} else if (index == 15) {
				temp = 20;
			}
			index = temp;
		}
		return index;
	}

	// 获取数值
	public int GetCardValue(int cbCardData) {
		return cbCardData & GameConstants.LOGIC_MASK_VALUE;
	}

	// 获取花色
	public int GetCardColor(int cbCardData) {
		if (cbCardData == 0) {
			return -1;
		}
		if (cbCardData > this._zhu_value) {
			cbCardData -= this._zhu_value;
		}
		if (this.GetCardValue(cbCardData) == 15 || this.GetCardValue(cbCardData) == 14) {
			return this._zhu_type;
		}
		return (cbCardData & GameConstants.LOGIC_MASK_COLOR) >> 4;
	}

	public int GetCardColor_Count(int cbCardData[], int card_count, int color) {
		int count = 0;
		for (int i = 0; i < card_count; i++) {
			if (color != this._zhu_type) {
				if (GetCardColor(cbCardData[i]) == color && cbCardData[i] < this._zhu_value) {
					count++;
				}
			} else {
				if (cbCardData[i] > this._zhu_value) {
					count++;
				}
			}
		}
		return count;
	}

	public int GetZhu_Count(int cbCardData[], int card_count) {
		int count = 0;
		for (int i = 0; i < card_count; i++) {
			if (cbCardData[i] > this._zhu_value) {
				count++;
			}
		}
		return count;
	}

	// 洗牌
	public void random_card_data(int return_cards[], final int mj_cards[]) {
		int card_count = return_cards.length;
		int card_data[] = new int[card_count];
		for (int i = 0; i < card_count; i++) {
			card_data[i] = mj_cards[i];
		}
		random_cards(card_data, return_cards, card_count);

	}

	// 混乱准备
	private static void random_cards(int card_data[], int return_cards[], int card_count) {
		// 混乱扑克
		int bRandCount = 0, bPosition = 0;
		do {
			bPosition = (int) (RandomUtil.getRandomNumber(Integer.MAX_VALUE) % (card_count - bRandCount));
			return_cards[bRandCount++] = card_data[bPosition];
			card_data[bPosition] = card_data[card_count - bRandCount];
		} while (bRandCount < card_count);

	}

	/***
	 * //排列扑克
	 * 
	 * @param card_date
	 * @param card_count
	 * @return
	 */
	// 排列扑克
	public void SortCardList(int cbCardData[], int cbCardCount) {
		// 排序过虑
		if (cbCardCount == 0)
			return;
		// 转换数值
		int cbSortValue[] = new int[cbCardCount];
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == _chang_zhu_one
					|| this.GetCardValue(cbCardData[i]) == _chang_zhu_two) {
				cbSortValue[i] = (4 << 8) + GetCardLogicValue(cbCardData[i]);
			} else if (this.GetCardValue(cbCardData[i]) == _chang_zhu_four) {
				if (GetCardColor(cbCardData[i]) == this._zhu_type) {
					cbSortValue[i] = (4 << 5) + GetCardLogicValue(cbCardData[i]);
				} else {
					cbSortValue[i] = (4 << 4) + GetCardLogicValue(cbCardData[i]) + GetCardColor(cbCardData[i]);
				}

			} else if (this.GetCardValue(cbCardData[i]) == _chang_zhu_three) {
				if (GetCardColor(cbCardData[i]) == this._zhu_type) {
					cbSortValue[i] = (4 << 7) + GetCardLogicValue(cbCardData[i]);
				} else {
					cbSortValue[i] = (4 << 6) + GetCardLogicValue(cbCardData[i]) + GetCardColor(cbCardData[i]);
				}

			} else if (GetCardColor(cbCardData[i]) == this._zhu_type) {
				cbSortValue[i] = (4 << 4) + GetCardLogicValue(cbCardData[i]);
			} else {
				cbSortValue[i] = (GetCardColor(cbCardData[i]) << 4) + GetCardLogicValue(cbCardData[i]);
			}

		}

		// 排序操作
		boolean bSorted = true;
		int cbSwitchData = 0, cbLast = cbCardCount - 1;
		do {
			bSorted = true;
			for (int i = 0; i < cbLast; i++) {
				if ((cbSortValue[i] < cbSortValue[i + 1]) || ((cbSortValue[i] == cbSortValue[i + 1])
						&& (GetCardLogicValue(cbCardData[i]) < GetCardLogicValue(cbCardData[i + 1])))) {
					// 设置标志
					bSorted = false;

					// 扑克数据
					cbSwitchData = cbCardData[i];
					cbCardData[i] = cbCardData[i + 1];
					cbCardData[i + 1] = cbSwitchData;

					// 排序权位
					cbSwitchData = cbSortValue[i];
					cbSortValue[i] = cbSortValue[i + 1];
					cbSortValue[i + 1] = cbSwitchData;
				}
			}
			cbLast--;
		} while (bSorted == false);

		return;
	}

	// 删除扑克
	public boolean remove_cards_by_data(int cards[], int card_count, int remove_cards[], int remove_count) {

		// 检验数据
		if (card_count < remove_count)
			return false;

		// 定义变量
		int cbDeleteCount = 0;
		int cbTempCardData[] = new int[card_count];

		for (int i = 0; i < card_count; i++) {
			cbTempCardData[i] = cards[i];
		}

		// 置零扑克
		for (int i = 0; i < remove_count; i++) {
			for (int j = 0; j < card_count; j++) {
				if (remove_cards[i] == cbTempCardData[j]) {
					cbDeleteCount++;
					cbTempCardData[j] = 0;
					break;
				}
			}
		}

		// 成功判断
		if (cbDeleteCount != remove_count) {
			return false;
		}

		// 清理扑克
		int cbCardPos = 0;
		for (int i = 0; i < card_count; i++) {
			if (cbTempCardData[i] != 0)
				cards[cbCardPos++] = cbTempCardData[i];
		}

		return true;
	}

	public int GetCardScore(int cbCardData[], int cbCardCount) {
		int score = 0;
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == 5) {
				score += 5;
			} else if (this.GetCardValue(cbCardData[i]) == 10 || this.GetCardValue(cbCardData[i]) == 13) {
				score += 10;
			} else if (this.GetCardValue(cbCardData[i]) == 14 || this.GetCardValue(cbCardData[i]) == 15) {
				if (this.has_rule(GameConstants.GAME_RULE_YZSJ_300)) {
					if (this.GetCardValue(cbCardData[i]) == 14) {
						score += 20;
					} else if (this.GetCardValue(cbCardData[i]) == 15) {
						score += 30;
					}
				} else {
					score += 10;
				}
			}
		}
		return score;
	}

	// 分析牌型
	public void get_card_all_type(int cbCardData[], int cbCardCount, tagAnalyseCardType type_card, int type) {
		tagAnalyseIndexResult_Xpsj IndexResult = new tagAnalyseIndexResult_Xpsj();
		AnalysebCardDataToIndex(cbCardData, cbCardCount, IndexResult);
		if (type == SJConstants.XP_SJ_CT_SINGLE) {
			for (int color = 0; color < 3; color++) {
				for (int i = SJConstants.XP_SJ_MAX_INDEX - 1; i >= 0; i--) {
					if (IndexResult.card_index[color][i] > 0) {
						for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
							type_card.count[type_card.type_count] = 1;
							type_card.type[type_card.type_count] = SJConstants.XP_SJ_CT_SINGLE;
							type_card.card_data[type_card.type_count][j] = IndexResult.card_data[color][i][j];
							type_card.type_count++;
						}
					}
				}
			}

		} else {
			for (int color = 0; color < 3; color++) {
				for (int i = SJConstants.XP_SJ_MAX_INDEX - 1; i >= 0; i--) {
					if (IndexResult.card_index[color][i] == 1) {
						for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
							type_card.count[type_card.type_count] = 1;
							type_card.type[type_card.type_count] = SJConstants.XP_SJ_CT_SINGLE;
							type_card.card_data[type_card.type_count][j] = IndexResult.card_data[color][i][j];
							type_card.type_count++;
						}
					}
					if (IndexResult.card_index[color][i] == 2) {
						type_card.count[type_card.type_count] = 2;
						type_card.type[type_card.type_count] = SJConstants.XP_SJ_CT_DOUBLE;
						for (int j = 0; j < IndexResult.card_index[color][i]; j++) {
							type_card.card_data[type_card.type_count][j] = IndexResult.card_data[color][i][j];
						}
						type_card.type_count++;
					}
				}
			}

		}
	}

	public int get_chang_zhu_count(int cbCardData[], int cbCardCount) {
		int count = 0;
		for (int i = 0; i < cbCardCount; i++) {
			if (this.GetCardValue(cbCardData[i]) == this._chang_zhu_one
					|| this.GetCardValue(cbCardData[i]) == this._chang_zhu_two
					|| this.GetCardValue(cbCardData[i]) == this._chang_zhu_three
					|| this.GetCardValue(cbCardData[i]) == this._chang_zhu_four) {
				count++;
			}
		}
		return count;
	}

	public boolean has_rule(int cbRule) {
		return ruleMap.containsKey(cbRule);
	}

}