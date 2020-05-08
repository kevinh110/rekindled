/* 
  Based on code by misha-codes: https://github.com/misha-codes/eloquent-javascript--level-editor
  This Javascript file has been heavily edited from the original code to fit ReKindle.
*/

/*//////////////////////////////////////////////////////////////////////////////
-                                   MENU                                       -
//////////////////////////////////////////////////////////////////////////////*/
function showDialog(fields, confirm, help = '') {
  removeCurrentDialog();

  let dialog = document.createElement('span');
  dialog.className = 'dialog';

  for (let {type, description} of fields) {
    let field = document.createElement(type);
    field.className = 'field';
    dialog.appendChild(document.createTextNode(` ${description} `));
    dialog.appendChild(field);
  }
  dialog.appendChild(document.createTextNode(' '));

  let yes = createButton(
    {'innerHTML': '<i class="material-icons md-18">check</i>'},
    confirm
  );
  dialog.appendChild(yes);

  let no = createButton(
    {'innerHTML': '<i class="material-icons md-18">close</i>'},
    removeCurrentDialog
  );
  dialog.appendChild(no);

  info.textContent = help;
  menu.appendChild(dialog);
}
function removeCurrentDialog() {
  let dialog = document.querySelector('.dialog');
  if (dialog) dialog.remove();
}

function createButton(label, onClick, className = '', help) {
  let button = document.createElement('button');
  button.className = className;
  Object.assign(button, label); // textContent or innerHTML

  if (help) {
    button.setAttribute('data-help', help);
    button.addEventListener('mouseenter', () => {
      info.textContent = button.getAttribute('data-help');
    });
  }
  button.addEventListener('click', onClick);
  return button;
}

let menu = document.querySelector('#menu');

/*```````````````````````````````````new``````````````````````````````````````*/
let newButton = createButton({'textContent': 'new'}, () => showDialog(
  [
    {type: 'input', description: 'width'},
    {type: 'input', description: 'height'}
  ],
  confirmNew, 'please enter width and height in cells'
));
menu.appendChild(newButton);

function confirmNew() {
  let fields = document.querySelectorAll('.field');
  let width = Number(fields[0].value);
  let height = Number(fields[1].value);
  if (!isNaN(width) && !isNaN(height)) {
    removeCurrentDialog();
    renderView(width, height);
  }
}

/*`````````````````````````````````open``````````````````````````````````````*/
let openButton = createButton({'textContent': 'open'}, () => showDialog(
  [{type: 'textarea', description: 'level'}],
  confirmOpen, 'please paste a level json to parse'
));
menu.appendChild(openButton);

function confirmOpen() {
  let width = 32;
  let height = 18;
  let player = [];
  let numInitLights = 0;
  let walls = [];
  let enemies = [];
  let litLights = [];
  let dimLights = [];
  let grass = [];
  let mushrooms = [];
  let holes = [];
  let pickup = [];

  let level = document.querySelector('textarea').value.trim().split('\n');
  for (let i = 0; i < level.length; i++) {

    // Parse Dimension
    if (level[i].search(/dimension/) > -1){
      let match = level[i].match(/([0-9]+),([0-9]+)/g)[0].split(',');
      width = Number(match[0]);
      height = Number(match[1]);
      // console.log('width: '+width);
      // console.log('height: '+height);
    }

    // Parse Player
    if (level[i].search(/spawn/) > -1){
      let match = level[i].match(/([0-9]+),([0-9]+)/g)[0].split(',');
      player.push(Number(match[0]));
      player.push(Number(match[1]));
      // console.log('spawn: '+player);
    }

    // Parse Init Lights
    if (level[i].search(/init_lights/) > -1){
      let match = level[i].match(/[0-9]+/)[0];
      numInitLights = Number(match);
      // console.log('init_lights: '+numInitLights);
    }

    // Parse Lights
    if (level[i].search(/\"lights\"/) > -1){
      if(level[i].search(/\]/) == -1){
        let a = i+1;
        while (level[a].search(/\{/) > -1){
          if (level[a].search(/position/) > -1){
            let match = level[a].match(/([0-9]+),([0-9]+)/g)[0].split(',');
            let lit = level[a].match(/true|false/)[0];
            if(lit == 'true') litLights.push([Number(match[0]),Number(match[1])]);
            else dimLights.push([Number(match[0]),Number(match[1])]);
            // console.log(match);
          }
          a += 1;
        }
      }
      // console.log('lit lights:');
      // console.log(litLights);
      // console.log('dim lights:');
      // console.log(dimLights);
      // console.log('lit lights: '+litLights);
      // console.log('dim lights: '+dimLights);
    } 

    //Parse Enemies
    if (level[i].search(/enemies/) > -1){
      if(level[i].search(/\]/) == -1){
        let a = i+1;
        while (level[a].search(/\{/) > -1){
          if (level[a].search(/position/) > -1){
            let pos = level[a].match(/([0-9]+),([0-9]+)/g)[0].split(',');
            pos = [Number(pos[0]),Number(pos[1])];
            let type = level[a].match(/\"type\"\: [0-9]+/)[0];
            type = Number(type.match(/[0-9]+/)[0]);
            let wander = level[a].match(/\[(\[([0-9]+),([0-9]+)\]\,)+(\[([0-9]+),([0-9]+)\])+\]/)[0].split('],[');
            for(let i=0;i<wander.length;i++){
              wander[i] = wander[i].match(/([0-9]+),([0-9]+)/g)[0].split(',');
              wander[i] = [Number(wander[i][0]),Number(wander[i][1])];
            }
            // console.log(pos);
            // console.log(type);
            // console.log(wander);
            enemies.push([pos,type,wander]);
          }
          a += 1;
        }
      }
      // console.log('enemies:');
      // console.log(enemies);
    }
  

    // Parse Walls
    if (level[i].search(/walls/) > -1){
      if(level[i].search(/\]/) == -1){
        let a = i+1;
        while (level[a].search(/\{/) > -1){
          if (level[a].search(/position/) > -1){
            let match = level[a].match(/([0-9]+),([0-9]+)/g)[0].split(',');
            walls.push([Number(match[0]),Number(match[1])]);
            // console.log(match);
          }
          a += 1;
        }
      }
      // console.log('walls:');
      // console.log(walls);
    } 

    // Parse Grass
    if (level[i].search(/grass/) > -1){
      let matches = level[i].matchAll(/([0-9]+),([0-9]+)/g);
      for(const match of matches){
        grass.push([Number(match[1]),Number(match[2])]);
        // console.log(match);
      }
      // console.log(grass);
    } 

    // Parse Mushrooms
    if (level[i].search(/mushrooms/) > -1){
      let matches = level[i].matchAll(/([0-9]+),([0-9]+)/g);
      for(const match of matches){
        mushrooms.push([Number(match[1]),Number(match[2])]);
        // console.log(match);
      }
      // console.log(mushrooms);
    } 

    // Parse holes
    if (level[i].search(/water/) > -1){
      let matches = level[i].matchAll(/([0-9]+),([0-9]+)/g);
      for(const match of matches){
        holes.push([Number(match[1]),Number(match[2])]);
        // console.log(match);
      }
      // console.log(holes);
    } 

    // Parse light pickups
    if (level[i].search(/pickup/) > -1){
      let matches = level[i].matchAll(/([0-9]+),([0-9]+)/g);
      for(const match of matches){
        pickup.push([Number(match[1]),Number(match[2])]);
        // console.log(match);
      }
      // console.log(pickup);
    } 
  }

  // set number of init lights
  document.querySelector('#initLightsCurrent').textContent = "Currently: "+numInitLights;
  document.querySelector('#initLightsInput').value = numInitLights;

  // set number of enemies
  document.querySelector('#numEnemiesInput').value = enemies.length;
  createEnemyPalettePanel(enemies.length);


  removeCurrentDialog();
  renderView(width, height);

  let cells = Array.from(document.querySelectorAll('.cell'));
  // let content = level.join('');
  let content = [];
  for(let j=0;j<height;j++){
    content[j] = [];
    if(j==0 || j == height-1){
      for(let i=0;i<width;i++){
        content[j].push('W');
      }
    } else{
      content[j].push('W');
      for(let i=1;i<width-1;i++){
          content[j].push('.');
      }
      content[j].push('W');
    }
  }

  // add player
  // console.log(player);
  content[height-1-player[1]][player[0]] = 'P';

  // add lights
  for(let i=0;i<litLights.length;i++){
    // console.log(litLights[i]);
    content[height-1-litLights[i][1]][litLights[i][0]] = 'L';
  }
  for(let i=0;i<dimLights.length;i++){
    // console.log(dimLights[i]);
    content[height-1-dimLights[i][1]][dimLights[i][0]] = 'U';
  }

  // add walls
  for(let i=0;i<walls.length;i++){
    // console.log(walls[i]);
    content[height-1-walls[i][1]][walls[i][0]] = 'W';
  }

  // add grass
  for(let i=0;i<grass.length;i++){
    // console.log(grass[i]);
    content[height-1-grass[i][1]][grass[i][0]] = 'G';
  }

  // add mushrooms
  for(let i=0;i<mushrooms.length;i++){
    // console.log(mushrooms[i]);
    content[height-1-mushrooms[i][1]][mushrooms[i][0]] = 'M';
  }

  // add holes
  for(let i=0;i<holes.length;i++){
    // console.log(holes[i]);
    content[height-1-holes[i][1]][holes[i][0]] = 'H';
  }

  // add pickup
  for(let i=0;i<pickup.length;i++){
    // console.log(pickup[i]);
    content[height-1-pickup[i][1]][pickup[i][0]] = 'F';
  }

  // add enemies
  for(let i=0;i<enemies.length;i++){
    // console.log("enemies");
    // console.log(enemies[i]);
    content[height-1-enemies[i][0][1]][enemies[i][0][0]] = 'E'+(i+1);
    for(let j=0;j<enemies[i][2].length;j++){
      content[height-1-enemies[i][2][j][1]][enemies[i][2][j][0]] = '@'+(i+1);
    }
  }
  // console.log(content);

  // convert to 1d array
  content1d = [];
  for(let j=0;j<height;j++){
    for(let i=0;i<width;i++){
      content1d.push(content[j][i]);
    }
  }
  // console.log(content1d);


  try {
    cells.forEach((cell, i) => {
      let char = content1d[i];
      if (!PALETTE.hasOwnProperty(char)) {
        if(char.search(/E/)==-1 && char.search(/@/)==-1){
          throw new Error(`error: invalid character: '${char}'`);
        }
      }
      cell.textContent = char;
      cell.className = "cell darkzone";
      if(char != '.'){
        if(char.search(/E/)>-1){
          let img = document.createElement('img');
          img.src = "./assets/"+PALETTE['E'].img;
          img.className = "enemysprite";
          cell.appendChild(img);
        } else if(char.search(/\@/)>-1){
          let img = document.createElement('img');
          img.src = "./assets/"+PALETTE['@'].img;
          img.className = "wanderpointsprite";
          cell.appendChild(img);
        } else {
          let img = document.createElement('img');
          img.src = "./assets/"+PALETTE[char].img;
          img.className = PALETTE[char].class;
          cell.appendChild(img);
        }
      }
      
    });
    cells.forEach((cell, i) => {
      if (cell.textContent == 'L'){
        let row = cell.parentNode;
        let table = row.parentNode;
        index = Array.prototype.indexOf.call(row.children,cell);
        row_index = Array.prototype.indexOf.call(table.children,row);
        // console.log(row);
        // console.log(index);
        // console.log(table);
        // console.log(row_index);
        // console.log(row.children[index]);
        // row 0
        applyEdit(row.children[index-2], 'LZ');
        applyEdit(row.children[index-1], 'LZ');
        applyEdit(row.children[index], 'LZ');
        applyEdit(row.children[index+1], 'LZ');
        applyEdit(row.children[index+2], 'LZ');
        // row -1
        let prev_row = table.children[row_index-1];
        applyEdit(prev_row.children[index-1], 'LZ');
        applyEdit(prev_row.children[index], 'LZ');
        applyEdit(prev_row.children[index+1], 'LZ');
        // row +1
        let next_row = table.children[row_index+1];
        applyEdit(next_row.children[index-1], 'LZ');
        applyEdit(next_row.children[index], 'LZ');
        applyEdit(next_row.children[index+1], 'LZ');
        // row -2
        let prev_prev_row = table.children[row_index-2];
        applyEdit(prev_prev_row.children[index], 'LZ');
        // row +2
        let next_next_row = table.children[row_index+2];
        applyEdit(next_next_row.children[index], 'LZ');
        
      }
    });

    // connect border walls
    cells.forEach((cell,i)=>{
      let row = cell.parentNode;
      let table = row.parentNode;
      index = Array.prototype.indexOf.call(row.children,cell);
      row_index = Array.prototype.indexOf.call(table.children,row);

      if(row_index == 0){
        if(index == 0){
          cell.childNodes[1].src = "./assets/wall/dr-single.png";
        } else if(index == width-1){
          cell.childNodes[1].src = "./assets/wall/dl-single.png";
        } else{
          cell.childNodes[1].src = "./assets/wall/lr.png";
        }
      } else if(row_index == height-1){
        if(index == 0){
          cell.childNodes[1].src = "./assets/wall/ur-single.png";
        } else if(index == width-1){
        cell.childNodes[1].src = "./assets/wall/lr-single.png";
        } else{
          cell.childNodes[1].src = "./assets/wall/lr.png";
        }
      } else if(index == 0 || index == width-1){
          cell.childNodes[1].src = "./assets/wall/ud.png";
      }
    });
  }
  catch (e) {
    info.textContent = e.message;
    return;
  }
  info.textContent = 'level parsed successfully';
}

/*`````````````````````````````````copy``````````````````````````````````````*/
let copyButton = createButton({'textContent': 'copy'}, copyLevel);
menu.appendChild(copyButton);
function copyLevel() {
  let level = '{\n';
  let enemies = '[\n';
  let enemiesArray = [];
  let wanderPaths = [];
  let player = '';
  let lights = '[\n';
  let walls = '[\n';
  let grass = '[';
  let mushrooms = '[';
  let holes = '[';
  let pickup = '[';
  let cells = Array.from(document.querySelectorAll('.cell'));
  let height = document.querySelectorAll('#view > div').length;
  let width = document.querySelector('#view > div').childNodes.length;
  level += '"dimension": ['+width+','+height+'], \n';
  let j = height-1;

  cells.forEach((cell, i) => {
    if ((i + 1) % width != 0 && (i + 1) % width != 1 && j != height-1 && j != 0){
      if (cell.textContent == 'P'){
        player = '['+((i%width))+','+j+']';
      }
      if (cell.textContent.search(/E/)>-1){
        let idx = cell.textContent.match(/[0-9]+/)[0];
        // console.log(idx);
        enemiesArray[idx] = [(i%width),j];
        // console.log(enemiesArray[idx]);
        // console.log(enemiesArray);
      }
      if (cell.textContent == 'L'){
        if (lights != '[\n') lights += ', \n';
        lights += '{"position": ['+((i%width))+','+j+'],"lit": true}';
      }
      if (cell.textContent == 'U'){
        if (lights != '[\n') lights += ', \n';
        lights += '{"position": ['+((i%width))+','+j+'],"lit": false}';
      }
      if (cell.textContent == 'W'){
        if (walls != '[\n') walls += ', \n';
        walls += '{"position": ['+((i%width))+','+j+'],"movable": false}';
      }
      if (cell.textContent == 'M'){
        if (mushrooms != '[') mushrooms += ', ';
        mushrooms += '['+((i%width))+','+j+']';
      }
      if (cell.textContent == 'G'){
        if (grass != '[') grass += ', ';
        grass += '['+((i%width))+','+j+']';
      }
      if (cell.textContent == 'H'){
        if (holes != '[') holes += ', ';
        holes += '['+((i%width))+','+j+']';
      }
      if (cell.textContent == 'F'){
        if (pickup != '[') pickup += ', ';
        pickup += '['+((i%width))+','+j+']';
      }
    }
    if ((i + 1) % width == 0) {
      j -= 1;
    }
  });

  for(let i=0;i<enemiesArray.length;i++){
    wanderPaths.push([]);
  }

  j = height-1;
  cells.forEach((cell, i) => {
    // console.log(i);
    if ((i + 1) % width != 0 && (i + 1) % width != 1 && j != height-1 && j != 0){
      if (cell.textContent.search(/\@/)>-1){
        // console.log(cell.textContent);
        // console.log(cell.textContent.match(/[0-9]+/)[0]);
        // console.log(wanderPaths);
        wanderPaths[cell.textContent.match(/[0-9]+/)[0]].push([(i%width),j]);
      }
    }
    if ((i + 1) % width == 0) {
      j -= 1;
    }
  });

  // create enemies string from the array
  for(let i=1;i<enemiesArray.length;i++){
    if (enemies != '[\n') enemies += ', \n';
    // console.log(i);
    // console.log(enemiesArray[i]);
    enemies += '{"position": ['+enemiesArray[i][0]+','+enemiesArray[i][1]+'],"type": 0,"wander": [';
    // console.log(wanderPaths[i]);
    if(wanderPaths[i].length >0){
      // console.log("inside if");
      // enemies += '['+enemiesArray[i][0]+','+enemiesArray[i][1]+'],['+wanderPaths[i][0]+']';
      enemies += '['+wanderPaths[i][0]+']';
      for(let j=1;j<wanderPaths[i].length;j++){
        enemies += ',['+wanderPaths[i][j]+']';
      }
    }
    enemies += ']}';
  }

  enemies += '\n]';
  lights += '\n]';
  walls += '\n]';
  grass += ']';
  holes += ']';
  pickup += ']';
  mushrooms += ']';
  let initLights = document.querySelector('#initLightsCurrent').textContent;
  initLights = initLights.match(/[0-9]+/)[0];
  // console.log(initLights);
  level += '"spawn": '+player+', \n';
  level += '"init_lights": '+initLights+', \n';
  level += '"lights": '+lights+', \n';
  level += '"enemies": '+enemies+', \n';
  level += '"walls": '+walls+', \n';
  level += '"grass": '+grass+', \n';
  level += '"mushrooms": '+mushrooms+', \n';
  level += '"water": '+holes+', \n';
  level += '"pickup": '+pickup+'\n';
  level += '}';
  info.textContent = level;
  info.select();
  document.execCommand('Copy');
  info.textContent = 'level copied to clipboard';
}

/*//////////////////////////////////////////////////////////////////////////////
-                          PALETTE & EDITING TOOLS                             -
//////////////////////////////////////////////////////////////////////////////*/
const PALETTE = {
  '.': {color: 'rgb(255, 255, 255)', help: 'empty', class: 'darkzone', img: 'background_unlit.png'},
  'W': {color: 'rgb(139,69,19)', help: 'wall', class: 'wallsprite', img: '/wall/singular.png'},
  'U': {color: 'rgb(169,169,169)', help: 'unlit light source', class: 'lampsprite', img: 'lamp_unlit.png'},
  'L': {color: 'rgb(241, 229, 89)', help: 'lit light source', class: 'lampsprite', img: 'lamp_lit.png'},
  'F': {color: 'rgb(241, 229, 89)', help: 'light pickup', class: 'pickupsprite', img: 'pickup.png'},
  'P': {color: 'rgb(153,102,204)', help: 'player', class: 'playersprite', img: 'player.png'},
  'H': {color: 'rgb(52, 166, 251)', help: 'hole', class: 'holesprite', img: '/hole/single.png'},
  'G': {color: 'rgb(50,205,50)', help: 'grass', class: 'sprite', img: 'grass_lit.png'},
  'M': {color: 'rgb(127,255,212)', help: 'mushrooms', class: 'sprite', img: 'mushrooms_lit.png'},
  'E': {color: 'rgb(255, 100, 100)', help: 'enemy', class: 'enemysprite', img: 'enemy.png'},
  '@': {color: 'rgb(255, 100, 100)', help: 'enemy wander path', class: 'wanderpointsprite', img: 'wander_point_marker.png'}
};
const WALLS = {
  's': {src:'singular.png', type:'single', help:'singular'},
  'std': {src:'d.png', type:'single', help:'top deadend'},
  'sbd': {src:'u.png', type:'single', help:'bottom deadend'},
  'srd': {src:'l.png', type:'single', help:'right deadend'},
  'sld': {src:'r.png', type:'single', help:'left deadend'},
  'surc': {src:'dl-single.png', type:'single', help:'upper right corner'},
  'sulc': {src:'dr-single.png', type:'single', help:'upper left corner'},
  'sbrc': {src:'lr-single.png', type:'single', help:'bottom right corner'},
  'sblc': {src:'ur-single.png', type:'single', help:'bottom left corner'},
  'sh': {src:'lr.png', type:'single', help:'horizontal'},
  'sv': {src:'ud.png', type:'single', help:'vertical'},

  'm': {src:'udlr.png', type:'multiple', help:'no edges'},
  'murc': {src:'dl.png', type:'multiple', help:'upper right corner'},
  'mulc': {src:'dr.png', type:'multiple', help:'upper left corner'},
  'mbrc': {src:'ul.png', type:'multiple', help:'bottom right corner'},
  'mblc': {src:'ur.png', type:'multiple', help:'bottom left corner'},
  'mteh': {src:'dlr.png', type:'multiple', help:'top edge horizontal'},
  'mbeh': {src:'ulr.png', type:'multiple', help:'bottom edge horizontal'},
  'mrev': {src:'udl.png', type:'multiple', help:'right edge vertical'},
  'mlev': {src:'udr.png', type:'multiple', help:'left edge vertical'} 
};
const TOOLS = [
  {tool: brush, icon: 'brush'},
  {tool: fill, icon: 'format_color_fill'}
];

let mouseTool = {tool: brush, char: '.'};

function applyEdit(cell, char) {
  // console.log("applyEdit");
  // console.log(char);

  if(char.search(/E/)>-1){
    cell.textContent = char;
    // cell.className = "cell enemy";
    // cell.style.backgroundColor = PALETTE['E'].color;
    let img = document.createElement('img');
    img.src = "./assets/"+PALETTE['E'].img;
    img.className = PALETTE['E'].class;
    cell.appendChild(img);
  } else if(char.search(/\@/)>-1){
    cell.textContent = char;
    // cell.style.backgroundColor = PALETTE['@'].color;
    let img = document.createElement('img');
    img.src = "./assets/"+PALETTE['@'].img;
    img.className = PALETTE['@'].class;
    cell.appendChild(img);
    
  // } else if(char == 'H' && cell.className == 'cell litzone'){
  //   cell.textContent = char;
  //   let img = document.createElement('img');
  //   img.src = "./assets/water_lit.png";
  //   img.className = PALETTE[char].class;
  //   cell.appendChild(img);
  } else if(char == 'F' || char == 'H' || char =='P' || char == 'U' || char == 'L' || char == 'M' || char == 'G'){
    cell.textContent = char;
    let img = document.createElement('img');
    img.src = "./assets/"+PALETTE[char].img;
    img.className = PALETTE[char].class;
    cell.appendChild(img);
  // } else if(char == 'L'){
  //   cell.textContent = char;
  //   cell.className = "cell lit";
  } else if(char =='W'){
    cell.textContent = char;

    // TODO: CONNECT WALLS
    // check if exist surrounding walls
    // let row = cell.parentNode;
    // let table = row.parentNode;
    // index = Array.prototype.indexOf.call(row.children,cell);
    // row_index = Array.prototype.indexOf.call(table.children,row);
    // // console.log(row_index+","+index);
    // let box = [
    //   [false, false, false],
    //   [false, true, false],
    //   [false, false, false]
    // ];
    // for(let i=-1;i<=1;i++){
    //   let row_i = table.childNodes[row_index+i];
    //   console.log(row_i);
    //   for(let j=-1;j<=1;j++){
    //     let cell_j = row_i.childNodes[index+j];
    //     if(cell_j.textContent == 'W'){
    //       box[i+1][j+1] = true;
    //     }
    //   }
    // }
    
    // console.log(box);

    let img = document.createElement('img');
    img.src = "./assets/"+PALETTE[char].img;
    img.className = PALETTE[char].class;
    cell.appendChild(img);
  } else if(char == 'LZ'){
    // change tiles to light tiles
    cell.className = "cell litzone";
    // change sprites to light sprites
    if(cell.textContent.search(/E/)>-1){
      cell.childNodes[1].src = './assets/saved_soul.png';
      cell.childNodes[1].className = 'soulsprite';
    // } else if(cell.textContent == 'H'){
    //   cell.childNodes[1].src = './assets/water_lit.png';
    }
  } else if(char == 'DZ') {
    // change tiles to dark tiles
    cell.className = "cell darkzone";
    // change sprites to dark sprites
    if(cell.textContent.search(/E/)>-1){
      cell.childNodes[1].src = "./assets/"+PALETTE['E'].img;
      cell.childNodes[1].className = PALETTE['E'].class;
    // } else if(cell.textContent == 'H'){
    //   cell.childNodes[1].src = './assets/water_dark.png';
    }
  } else if(char == '.'){
    if(cell.textContent == 'L'){
      cell.className = "cell darkzone";
      // cell.parentNode.className = "cell darkzone";
      cell.removeChild(cell.childNodes[1]);
      // console.log(cell.parentNode.childNodes[1]);
      // cell.parentNode.remove(cell);
      cell.textContent = '.';
    } else if(cell.textContent == 'W' || cell.textContent == 'U' || cell.textContent == 'P' || cell.textContent == 'G' || cell.textContent == 'M' || cell.textContent == 'H' || cell.textContent.search(/E/)>-1 || cell.textContent.search(/@/)>-1) {
      cell.removeChild(cell.childNodes[1]);
      cell.textContent = '.';
    }
  } else {
    cell.textContent = char;
    cell.className = "cell darkzone";
  }

}

function brush(event) {
  if (event.buttons == 1 && mouseTool.char != event.target.textContent) {
    let cell = event.target;
    updateHistory([cell], cell.textContent);

    // console.log(cell.textContent);
    if (cell.className != 'cell darkzone' && cell.className != 'cell litzone'){
      cell = cell.parentNode;
    }
    // console.log(cell.textContent);
    // remove all surrounding light tiles
    // console.log(mouseTool.char);
    // console.log(cell.parentNode.textContent);
    if ((mouseTool.char != 'L') && cell.textContent == 'L'){
      // console.log("inside if");
      // cell = cell.parentNode;
      let row = cell.parentNode;
      let table = row.parentNode;
      index = Array.prototype.indexOf.call(row.children,cell);
      row_index = Array.prototype.indexOf.call(table.children,row);
      // console.log(row);
      // console.log(index);
      // console.log(table);
      // console.log(row_index);
      // console.log(row.children[index]);
      // row 0
      applyEdit(row.children[index-2], 'DZ');
      applyEdit(row.children[index-1], 'DZ');
      applyEdit(row.children[index], 'DZ');
      applyEdit(row.children[index+1], 'DZ');
      applyEdit(row.children[index+2], 'DZ');
      // row -1
      let prev_row = table.children[row_index-1];
      applyEdit(prev_row.children[index-1], 'DZ');
      applyEdit(prev_row.children[index], 'DZ');
      applyEdit(prev_row.children[index+1], 'DZ');
      // row +1
      let next_row = table.children[row_index+1];
      applyEdit(next_row.children[index-1], 'DZ');
      applyEdit(next_row.children[index], 'DZ');
      applyEdit(next_row.children[index+1], 'DZ');
      // row -2
      let prev_prev_row = table.children[row_index-2];
      applyEdit(prev_prev_row.children[index], 'DZ');
      // row +2
      let next_next_row = table.children[row_index+2];
      applyEdit(next_next_row.children[index], 'DZ');
      
    }

    applyEdit(cell, mouseTool.char);

    // add surrounding light tiles
    if (mouseTool.char == 'L'){
      let row = cell.parentNode;
      let table = row.parentNode;
      index = Array.prototype.indexOf.call(row.children,cell);
      row_index = Array.prototype.indexOf.call(table.children,row);
      // console.log(row);
      // console.log(index);
      // console.log(table);
      // console.log(row_index);
      // console.log(row.children[index]);
      // row 0
      applyEdit(row.children[index-2], 'LZ');
      applyEdit(row.children[index-1], 'LZ');
      applyEdit(row.children[index], 'LZ');
      applyEdit(row.children[index+1], 'LZ');
      applyEdit(row.children[index+2], 'LZ');
      // row -1
      let prev_row = table.children[row_index-1];
      applyEdit(prev_row.children[index-1], 'LZ');
      applyEdit(prev_row.children[index], 'LZ');
      applyEdit(prev_row.children[index+1], 'LZ');
      // row +1
      let next_row = table.children[row_index+1];
      applyEdit(next_row.children[index-1], 'LZ');
      applyEdit(next_row.children[index], 'LZ');
      applyEdit(next_row.children[index+1], 'LZ');
      // row -2
      let prev_prev_row = table.children[row_index-2];
      applyEdit(prev_prev_row.children[index], 'LZ');
      // row +2
      let next_next_row = table.children[row_index+2];
      applyEdit(next_next_row.children[index], 'LZ');
      
    }    
  }
}

function fill(event) {
  if (
    event.buttons == 1 &&
    event.type == 'mousedown' &&
    mouseTool.char != event.target.textContent
  ) {
    let rows = Array.from(view.children);
    let grid = rows.map(row => Array.from(row.children));
    let width = grid[0].length, height = grid.length;

    let startCell = event.target;
    let startY = rows.indexOf(startCell.parentNode);
    let startX = grid[startY].indexOf(startCell);

    let targetChar = startCell.textContent;
    let queue = [{cell: startCell, x: startX, y: startY}];
    let changed = [];

    function flood(cell, x, y) {
      if (cell.textContent != targetChar) return ;
      for (let westX = x; westX >= 0; westX--) {
        let currentNeighbor = grid[y][westX];
        if (currentNeighbor.textContent == targetChar) {
          applyEdit(currentNeighbor, mouseTool.char);
          changed.push(currentNeighbor);
          if (y > 0 && grid[y - 1][westX].textContent == targetChar) {
            queue.push({cell: grid[y - 1][westX], x: westX, y: y - 1});
          }
          if (y < height - 1 && grid[y + 1][westX].textContent == targetChar) {
            queue.push({cell: grid[y + 1][westX], x: westX, y: y + 1});
          }
        }
        else break;
      }
      for (let eastX = x + 1; eastX < width; eastX++) {
        let currentNeighbor = grid[y][eastX];
        if (currentNeighbor.textContent == targetChar) {
          applyEdit(currentNeighbor, mouseTool.char);
          changed.push(currentNeighbor);
          if (y > 0  &&  grid[y - 1][eastX].textContent == targetChar) {
            queue.push({cell: grid[y - 1][eastX], x: eastX, y: y - 1});
          }
          if (y < height - 1 && grid[y + 1][eastX].textContent == targetChar) {
            queue.push({cell: grid[y + 1][eastX], x: eastX, y: y + 1});
          }
        }
        else break;
      }
    }

    for (let {cell, x, y} of queue) {
      flood(cell, x, y);
    }
    updateHistory(changed, targetChar);
  }
}

/*//////////////////////////////////////////////////////////////////////////////
-                                 TOOLBAR                                      -
//////////////////////////////////////////////////////////////////////////////*/
let toolbar = document.querySelector('#toolbar');

/*................................tool buttons................................*/
let toolPanel = document.createElement('div');
toolPanel.style.paddingBottom = '1px';
let toolButtons = [];

for (let {tool, icon} of TOOLS) {
  let button = createButton(
    {'innerHTML': `<i class="material-icons">${icon}</i>`},
    () => {
      toolButtons.forEach(b => b.className = 'tool');
      button.className = 'tool selected';
      mouseTool.tool = tool;
    },
    'tool', `${tool.name}`
  );
  toolButtons.push(button);
  toolPanel.appendChild(button);
}
toolbar.appendChild(toolPanel);

/*..............................palette buttons...............................*/
let chars = Object.keys(PALETTE);
let paletteButtons = [];
let palettePanel = document.createElement('div');
palettePanel.style.borderTop = '1px solid rgb(64, 64, 64)';
palettePanel.style.borderBottom = palettePanel.style.borderTop;
palettePanel.style.paddingTop = '1px';
palettePanel.style.paddingBottom = palettePanel.style.paddingTop;
for (let i = 0, row = document.createElement('div'); i < chars.length; i++) {
  let char = chars[i];
  if(char.search(/E/)==-1 && char.search(/\@/)==-1){
    let button = createButton({'textContent': char}, () => {
      paletteButtons.forEach(b => b.className = 'palette');
      button.className = 'palette selected';
      mouseTool.char = button.textContent;
    }, 'palette', PALETTE[char].help);

    paletteButtons.push(button);
    row.appendChild(button);
  }
  if ((i + 1) % 4 == 0 || i == chars.length - 1) {
    palettePanel.appendChild(row);
    row = document.createElement('div');
  }
}
toolbar.appendChild(palettePanel);

/*//////////////////////////////////////////////////////////////////////////////
-                                  UNDO/REDO                                   -
//////////////////////////////////////////////////////////////////////////////*/
class HistoryStack {
  constructor() {
    this._stack = []; //[{[cell, ...], char}, ...]
  }
  push(record) {
    this._stack.push(record);
    document.dispatchEvent(HistoryStack.updateEvent());
  }
  pop() {
    let record = this._stack.pop()
    document.dispatchEvent(HistoryStack.updateEvent());
    return record;
  }
  clear() {
    this._stack = [];
    document.dispatchEvent(HistoryStack.updateEvent());
  }
  drop(last) {
    this._stack = this._stack.slice(0, last);
    document.dispatchEvent(HistoryStack.updateEvent());
  }
  get length() { return this._stack.length; }

  static updateEvent() { return new CustomEvent('historyupdate'); }
}

let past = new HistoryStack, future = new HistoryStack;

function updateHistory(cells, char) {
  future.clear();
  if (past.length > 1000) past.drop(500);
  past.push({cells, char});
}
function clearHistory() {
  future.clear();
  past.clear();
}

function undo() {
  if (!past.length) return;
  let record = past.pop();
  future.push({cells: record.cells, char: record.cells[0].textContent});
  record.cells.forEach(cell => applyEdit(cell, record.char));
}
function redo() {
  if(!future.length) return;
  let record = future.pop();
  past.push({cells: record.cells, char: record.cells[0].textContent});
  record.cells.forEach(cell => applyEdit(cell, record.char));
}

window.addEventListener('keydown', event => {
  if ((event.ctrlKey || event.metaKey) && event.key == 'z') {
    undo();
  }
});
window.addEventListener('keydown', event => {
  if ((event.ctrlKey || event.metaKey) && event.key == 'y') {
    redo();
  }
});

let undoButton = createButton(
  {'innerHTML': '<i class="material-icons">undo</i>'},
  undo, 'edit', 'undo (ctrl/cmd + z)'
);
undoButton.disabled = true;
// toolPanel.appendChild(undoButton);

let redoButton = createButton(
  {'innerHTML': '<i class="material-icons">redo</i>'},
  redo, 'edit', 'redo (ctrl/cmd + y)'
);
redoButton.disabled = true;
// toolPanel.appendChild(redoButton);

document.addEventListener('historyupdate', () => {
  if (past.length) undoButton.disabled = false;
  else undoButton.disabled = true;
  if (future.length) redoButton.disabled = false;
  else redoButton.disabled = true;
});

/*//////////////////////////////////////////////////////////////////////////////
-                            INFORMATION/FEEDBACK                              -
//////////////////////////////////////////////////////////////////////////////*/
let infoPanel = document.createElement('div');
infoPanel.style.paddingTop = '1px';
let info = document.createElement('textarea');
info.className = 'info';
info.rows = 3;
info.readOnly = true;
infoPanel.appendChild(info);
toolbar.appendChild(infoPanel);

let addSettingsPanel = document.createElement('div');
addSettingsPanel.style.paddingTop = '1px';

let initLightsPanel = document.createElement('div');
initLightsPanel.style.padding = '5px';
initLightsPanel.style.backgroundColor = 'rgb(241, 229, 89)';
let initLightsField = document.createElement('p');
initLightsField.className = 'fieldname';
initLightsField.textContent = "Number Init Lights";
initLightsPanel.appendChild(initLightsField);
let initLightsInput = document.createElement('input');
initLightsInput.className = 'numberInput';
initLightsInput.id = 'initLightsInput';
initLightsInput.setAttribute("type", "number");
initLightsInput.setAttribute("value", "0");
initLightsPanel.appendChild(initLightsInput);
let initLightsApply = createButton({'textContent': 'Apply'}, () => {
  document.querySelector('#initLightsCurrent').textContent = "Currently: "+initLightsInput.value;
}, 'applyButton', '');
initLightsPanel.appendChild(initLightsApply);
let initLightsCurrent = document.createElement('p');
initLightsCurrent.id = 'initLightsCurrent';
initLightsCurrent.className = 'fieldname';
initLightsCurrent.textContent = "Currently: 0";
initLightsPanel.appendChild(initLightsCurrent);
addSettingsPanel.appendChild(initLightsPanel);

let numEnemiesPanel = document.createElement('div');
numEnemiesPanel.style.padding = '5px';
numEnemiesPanel.style.marginTop = '1px';
numEnemiesPanel.style.backgroundColor = 'rgb(255, 100, 100)';
let numEnemiesField = document.createElement('p');
numEnemiesField.className = 'fieldname';
numEnemiesField.style.color = 'black';
numEnemiesField.textContent = "Number of Enemies";
numEnemiesPanel.appendChild(numEnemiesField);
let numEnemiesInput = document.createElement('input');
numEnemiesInput.className = 'numberInput';
numEnemiesInput.id = 'numEnemiesInput';
numEnemiesInput.setAttribute("type", "number");
numEnemiesInput.setAttribute("value", "2");
numEnemiesPanel.appendChild(numEnemiesInput);
let numEnemiesApply = createButton({'textContent': 'Apply'}, () => {
  createEnemyPalettePanel(numEnemiesInput.value);
}, 'applyButton', '');
numEnemiesPanel.appendChild(numEnemiesApply);
addSettingsPanel.appendChild(numEnemiesPanel);

toolbar.appendChild(addSettingsPanel);


function createEnemyPalettePanel(numEnemies){
  if(document.querySelector('#enemyPalette')){
    document.querySelector('#enemyPalette').remove();
  }
  let enemyPalettePanel = document.createElement('div');
  enemyPalettePanel.id = 'enemyPalette';
  enemyPalettePanel.style.borderTop = '1px solid rgb(64, 64, 64)';
  enemyPalettePanel.style.borderBottom = enemyPalettePanel.style.borderTop;
  enemyPalettePanel.style.paddingTop = '1px';
  enemyPalettePanel.style.paddingBottom = enemyPalettePanel.style.paddingTop;
  for (let i = 0, row = document.createElement('div'); i < numEnemies; i++) {
    let char = 'E'+(i+1);
    let button = createButton({'textContent': char}, () => {
      paletteButtons.forEach(b => b.className = 'palette');
      button.className = 'palette selected';
      mouseTool.char = button.textContent;
    }, 'palette', PALETTE['E'].help+' '+(i+1));

    paletteButtons.push(button);
    row.appendChild(button);

    let char2 = '@'+(i+1);
    let button2 = createButton({'textContent': char2}, () => {
      paletteButtons.forEach(b => b.className = 'palette');
      button2.className = 'palette selected';
      mouseTool.char = button2.textContent;
    }, 'palette', PALETTE['@'].help+' '+(i+1));

    paletteButtons.push(button2);
    row.appendChild(button2);

    if ((i + 1) % 4 == 0 || i == numEnemies-1) {
      enemyPalettePanel.appendChild(row);
      row = document.createElement('div');
    }
  }
  toolbar.appendChild(enemyPalettePanel);
}
createEnemyPalettePanel(2);

/*//////////////////////////////////////////////////////////////////////////////
-                                   VIEW                                       -
//////////////////////////////////////////////////////////////////////////////*/
let view = document.querySelector('#view');
view.style.left = `${toolbar.getBoundingClientRect().right + 8}px`;
function renderView(width, height) {
  clearHistory();
  Array.from(document.querySelectorAll('#view > div'))
    .forEach(div => div.remove());
  for (let y = 0; y < height; y++) {
    if (y == 0 || y == height-1){
      let row = document.createElement('div');
      for (let x = 0; x < width; x++) {
          let cell = document.createElement('div');
          cell.className = 'cell darkzone';
          cell.textContent = 'W';
          let img = document.createElement('img');
          img.src = "./assets/wall/singular.png";
          img.className = "wallsprite";
          cell.appendChild(img);
          cell.addEventListener('mousedown', edit);
          cell.addEventListener('mouseover', edit);
          row.appendChild(cell);
      }
      view.appendChild(row);
    } else {
      let row = document.createElement('div');
      for (let x = 0; x < width; x++) {
        if (x==0 || x==width-1){
          let cell = document.createElement('div');
          cell.className = 'cell darkzone';
          cell.textContent = 'W';
          let img = document.createElement('img');
          img.src = "./assets/wall/singular.png";
          img.className = "wallsprite";
          cell.appendChild(img);
          cell.addEventListener('mousedown', edit);
          cell.addEventListener('mouseover', edit);
          row.appendChild(cell);
        } else {
          let cell = document.createElement('div');
          cell.className = 'cell darkzone';
          cell.textContent = '.';
          cell.addEventListener('mousedown', edit);
          cell.addEventListener('mouseover', edit);
          row.appendChild(cell);
        }
      }
      view.appendChild(row);
    }
  }
  // connect border walls
  let cells = Array.from(document.querySelectorAll('.cell'));
  cells.forEach((cell,i)=>{
    let row = cell.parentNode;
    let table = row.parentNode;
    index = Array.prototype.indexOf.call(row.children,cell);
    row_index = Array.prototype.indexOf.call(table.children,row);

    if(row_index == 0){
      if(index == 0){
        cell.childNodes[1].src = "./assets/wall/dr-single.png";
      } else if(index == width-1){
        cell.childNodes[1].src = "./assets/wall/dl-single.png";
      } else{
        cell.childNodes[1].src = "./assets/wall/lr.png";
      }
    } else if(row_index == height-1){
      if(index == 0){
        cell.childNodes[1].src = "./assets/wall/ur-single.png";
      } else if(index == width-1){
      cell.childNodes[1].src = "./assets/wall/lr-single.png";
      } else{
        cell.childNodes[1].src = "./assets/wall/lr.png";
      }
    } else if(index == 0 || index == width-1){
        cell.childNodes[1].src = "./assets/wall/ud.png";
    }
  });
}
function edit(event) {
  mouseTool.tool(event);
}

/*//////////////////////////////////////////////////////////////////////////////
-                                INITIAL SETUP                                 -
//////////////////////////////////////////////////////////////////////////////*/
openButton.dispatchEvent(new MouseEvent('click'));
document.querySelector('.field').value = '{\n"dimension": [32,18], \n"spawn": [10,10], \n"init_lights": 0, \n"lights": [\n{"position": [6,14],"lit": true}, \n{"position": [16,9],"lit": false}, \n{"position": [5,5],"lit": true}\n], \n"enemies": [\n{"position": [5,15],"type": 0,"wander": [[4,15],[10,15],[10,5]]}, \n{"position": [27,5],"type": 0,"wander": [[30,5],[20,5]]}\n], \n"walls": [\n{"position": [6,5],"movable": false}, \n{"position": [6,4],"movable": false}\n],\n"grass": [[8,12], [9,12], [10,12]],\n"mushrooms": [[7,7], [8,7], [8,6]],\n"water": [[13,11], [13,10], [13,9]],\n"pickup": [[1,1],[2,2]]\n"}';

confirmOpen();
toolButtons[0].dispatchEvent(new MouseEvent('click'));
paletteButtons[1].dispatchEvent(new MouseEvent('click'));
info.textContent = '<ReKindle> Simple Text Level Editor';
