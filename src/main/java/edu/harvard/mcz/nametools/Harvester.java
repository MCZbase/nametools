/**
 * Harvester.java
 * 
 * Copyright 2015 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.harvard.mcz.nametools;

/**
 * Interface for data sources that can harvest taxon names.
 * The implementation is responsible for serialization of the
 * harvest to some output.
 * 
 * @author mole
 *
 */
public interface Harvester {
	
	public abstract void getAllChildren(String taxon);
	
	public abstract void harvestComplete();
}
