package com.andrew;

import java.util.List;

/**
 * The interface Za room mapper.
 *
 * @author tongwenjin
 * @since 2022 /11/18
 */
public interface ZaRoomMapper {

  /**
   * Select list list.
   *
   * @param room the room
   * @return the list
   */
  List<ZaRoom> selectList(ZaRoom room);
}
