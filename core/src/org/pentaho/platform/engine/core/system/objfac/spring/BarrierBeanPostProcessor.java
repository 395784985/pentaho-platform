/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2015 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.platform.engine.core.system.objfac.spring;

import java.util.Set;

import org.pentaho.platform.servicecoordination.api.IServiceBarrier;
import org.pentaho.platform.servicecoordination.api.IServiceBarrierManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * This class is registered with spring. Spring calls the implemented methods just before, and just after, instantiation
 * of a bean. 
 * 
 * @author tkafalas
 */
public class BarrierBeanPostProcessor implements BeanPostProcessor {
  static IServiceBarrierManager serviceBarrierManager;
  
  private static BarrierBeanPostProcessor barrierBeanPostProcessor;

  private BarrierBeanPostProcessor() {
    serviceBarrierManager = IServiceBarrierManager.LOCATOR.getManager();
  }

  public static BarrierBeanPostProcessor getInstance() {
    if ( barrierBeanPostProcessor == null ) {
      barrierBeanPostProcessor = new BarrierBeanPostProcessor();
    }
    return barrierBeanPostProcessor;
  }

  @Override
  public Object postProcessBeforeInitialization( Object bean, String beanName ) throws BeansException {
    return bean;
  }

  @Override
  /**
   * If a bean is initialized that is registered with the {{@link #BarrierBeanProcessor}, then one hold on that
   * barrier will be released.
   */
  public Object postProcessAfterInitialization( Object bean, String beanName ) throws BeansException {
    Set<String> barriers = BarrierBeanProcessor.getInstance().getBeanBarriers().get( beanName );
    if ( barriers != null ) {
      for ( String barrierName : barriers ) {
        IServiceBarrier barrier = serviceBarrierManager.getServiceBarrier( barrierName );
        barrier.release();
      }
    }
    return bean;
  }

}