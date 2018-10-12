package com.cai.future.runnable;

import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cai.common.define.ELogType;
import com.cai.common.domain.Room;
import com.cai.common.util.ThreadUtil;
import com.cai.dictionary.SysGameTypeDict;
import com.cai.future.BaseFuture;
import com.cai.game.pdk.PDKTable;
import com.cai.service.MongoDBServiceImpl;
import com.cai.service.PlayerServiceImpl;

public class PDKAutoYinBaoRunnable extends BaseFuture {

	private static Logger logger = LoggerFactory.getLogger(PDKAutoYinBaoRunnable.class);

	private int _room_id;
	private int _seat_index;
	private PDKTable _table;
	private int _out_type;
	private int _out_count;
	private int[] _out_card = new int [4];

	public PDKAutoYinBaoRunnable(int room_id, int seat_index, PDKTable table, int out_type,int [] cards_data,int out_count) {
		super(room_id);
		_room_id = room_id;
		_seat_index = seat_index;
		_table = table;
		_out_type = out_type;
		_out_count = out_count;
		for(int i = 0;i < out_count;i++ ){
			_out_card[i] = cards_data[i];
		}
	}

	@Override
	public void execute() {
		try {
			Room table = PlayerServiceImpl.getInstance().getRoomMap().get(_room_id);
			if (table == null) {
				logger.info("调度发牌失败,房间[" + _room_id + "]不存在");
				return;
			}
			// logger.info("调度发牌,房间["+_room_id+"]");
			ReentrantLock roomLock = table.getRoomLock();
			try {
				roomLock.lock();
				_table.auto_yin_bao(_seat_index, _out_type, _out_card, _out_count);
			} finally {
				roomLock.unlock();

			}

		} catch (Exception e) {
			logger.error("error" + _room_id, e);
			Room room = PlayerServiceImpl.getInstance().getRoomMap().get(_room_id);
			if(room!=null) {
				MongoDBServiceImpl.getInstance().server_error_log(room.getRoom_id(), ELogType.roomLogicError, ThreadUtil.getStack(e),
						0L, SysGameTypeDict.getInstance().getGameDescByTypeIndex(room.getGameTypeIndex()), room.getGame_id());
			}
		}

	}

}